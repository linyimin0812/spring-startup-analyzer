package io.github.linyimin0812.profiler.extension.enhance.sample.jvmprofiler;

import io.github.linyimin0812.profiler.common.utils.NameUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author linyimin
 **/
public class FlameGraph {
    public static final byte FRAME_JIT_COMPILED = 0;
    public static final byte FRAME_NATIVE = 1;

    private static final String FLAME_TEMPLATE_FILE_NAME = "flame-graph.html";

    private final Frame root = new Frame(FRAME_NATIVE);
    private int depth;


    public void parse(String flameTemplatePath, String outputPath, Map<String, Integer> traceMap) throws IOException {

        for (Map.Entry<String, Integer> entry : traceMap.entrySet()) {
            String[] trace = entry.getKey().split(";");
            addSample(trace, entry.getValue());
        }
        dump(flameTemplatePath, outputPath);
    }
    private void addSample(String[] trace, long ticks) {

        Frame frame = root;
        for (String s : trace) {
            frame = frame.addChild(s, ticks);
        }
        frame.addLeaf(ticks);

        depth = Math.max(depth, trace.length);
    }

    private void dump(String flameTemplatePath, String outputPath) throws IOException {

        Path destPath = Paths.get(outputPath);
        Files.deleteIfExists(destPath);
        Files.createDirectories(destPath.getParent());
        Files.createFile(destPath);

        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(destPath), 32768);
             PrintStream out = new PrintStream(bos, false, "UTF-8")) {
            dump(flameTemplatePath, out);
        }
    }

    private void dump(String flameTemplatePath, PrintStream out) {
        int depth = this.depth + 1;

        String tail = getFlameGraphResource(flameTemplatePath);

        tail = printTill(out, tail, "/*height:*/300");
        out.print(Math.min(depth * 16, 32767));

        tail = printTill(out, tail, "/*title:*/");
        out.print("Wall clock profile");

        tail = printTill(out, tail, "/*reverse:*/false");
        out.print(false);

        tail = printTill(out, tail, "/*depth:*/0");
        out.print(depth);

        tail = printTill(out, tail, "/*frames:*/");

        printFrame(out, "all", root, 0, 0);

        tail = printTill(out, tail, "/*highlight:*/");
        out.print("");

        out.print(tail);
    }

    private String printTill(PrintStream out, String data, String till) {
        int index = data.indexOf(till);
        out.print(data.substring(0, index));
        return data.substring(index + till.length());
    }

    private void printFrame(PrintStream out, String title, Frame frame, int level, long x) {
        int type = frame.getType();

        if ((frame.inlined | frame.c1 | frame.interpreted) != 0 && frame.inlined < frame.total && frame.interpreted < frame.total) {
            out.println("f(" + level + "," + x + "," + frame.total + "," + type + ",'" + escape(title) + "'," +
                    frame.inlined + "," + frame.c1 + "," + frame.interpreted + ")");
        } else {
            out.println("f(" + level + "," + x + "," + frame.total + "," + type + ",'" + escape(title) + "')");
        }

        x += frame.self;
        for (Map.Entry<String, Frame> e : frame.entrySet()) {
            Frame child = e.getValue();
            if (child.total >= 0) {
                printFrame(out, e.getKey(), child, level + 1, x);
            }
            x += child.total;
        }
    }

    private static String escape(String s) {
        if (s.indexOf('\\') >= 0) s = s.replace("\\", "\\\\");
        if (s.indexOf('\'') >= 0) s = s.replace("'", "\\'");
        return s;
    }

    private static String getFlameGraphResource(String flameTemplatePath) {

        try (InputStream stream = Files.newInputStream(Paths.get(flameTemplatePath))) {

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[64 * 1024];
            for (int length; (length = stream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException("Can't load resource with name " + Paths.get(NameUtil.getTemplatePath() , FLAME_TEMPLATE_FILE_NAME));
        }
    }

    static class Frame extends TreeMap<String, Frame> {
        final byte type;
        long total;
        long self;
        long inlined, c1, interpreted;

        Frame(byte type) {
            this.type = type;
        }

        byte getType() {
            return type;
        }

        private Frame getChild(String title, byte type) {
            Frame child = super.get(title);
            if (child == null) {
                super.put(title, child = new Frame(type));
            }
            return child;
        }

        Frame addChild(String title, long ticks) {
            total += ticks;

            Frame child;

            if (StringUtils.contains(title, ".Unsafe.")) {
                child = getChild(title, FRAME_NATIVE);
            } else {
                child = getChild(title, FRAME_JIT_COMPILED);
            }

            return child;
        }

        void addLeaf(long ticks) {
            total += ticks;
            self += ticks;
        }
    }
}
