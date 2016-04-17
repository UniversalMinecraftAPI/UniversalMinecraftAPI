package com.koenv.jsonapi.docgenerator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.koenv.jsonapi.docgenerator.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    @Parameter
    private List<File> files = new ArrayList<>();

    @Parameter(names = {"-extra", "-e"})
    private String extraDirectoryPath = "docs";

    public static void main(String[] args) {
        Main main = new Main();
        new JCommander(main, args);
        main.run();
    }

    private void run() {
        boolean valid = true;

        if (files.size() < 1) {
            logger.error("Please specify at least 1 API file");
            valid = false;
        }

        for (File file : files) {
            if (!file.exists() || !file.isFile()) {
                logger.error("File " + file.getPath() + " doesn't exist or isn't a file.");
                valid = false;
            }
        }

        File extraDirectory = new File(extraDirectoryPath);
        if (!extraDirectory.exists() || !extraDirectory.isDirectory()) {
            logger.error("Extra directory " + extraDirectory.getPath() + " doesn't exist or isn't a directory.");
            valid = false;
        }

        if (!valid) {
            return;
        }

        List<PlatformMethods> methods = new ArrayList<>();
        for (File file : files) {
            APIFileParser parser = new APIFileParser(file);
            try {
                methods.add(parser.parse());
            } catch (Exception e) {
                logger.error("Failed to parse file " + file.getPath(), e);
                return;
            }
        }

        if (methods.size() == 2) {
            PlatformMethods platform1 = methods.get(0);
            PlatformMethods platform2 = methods.get(1);
            MethodDiffResult<NamespacedMethod> namespacedMethodDiffResult = MethodDiffResult.diff(
                    platform1.getPlatform(),
                    platform1.getNamespacedMethods(),
                    platform2.getPlatform(),
                    platform2.getNamespacedMethods()
            );

            MethodDiffResult<ClassMethod> classMethodDiffResult = MethodDiffResult.diff(
                    platform1.getPlatform(),
                    platform1.getClassMethods(),
                    platform2.getPlatform(),
                    platform2.getClassMethods()
            );

            List<AbstractMethod> onlyInPlatform1 = new ArrayList<>(namespacedMethodDiffResult.getOnlyInPlatform1());
            onlyInPlatform1.addAll(classMethodDiffResult.getOnlyInPlatform1());

            List<AbstractMethod> onlyInPlatform2 = new ArrayList<>(classMethodDiffResult.getOnlyInPlatform2());
            onlyInPlatform2.addAll(classMethodDiffResult.getOnlyInPlatform2());

            printDiffWarnings(platform1.getPlatform(), onlyInPlatform1);
            printDiffWarnings(platform2.getPlatform(), onlyInPlatform2);

            printStreamDiffWarnings(platform1.getPlatform(), platform1.getStreams(), platform2.getPlatform(), platform2.getStreams());
        } else {
            logger.warn("Not yet possible to gather information about possible method diffs");
        }
    }

    private <T extends AbstractMethod> void printDiffWarnings(Platform platform, List<T> methods) {
        if (methods.size() > 0) {
            logger.warn(methods.size() + " methods only available in " + platform.getName() + ":");
            for (AbstractMethod method : methods) {
                logger.warn("* " + method.getDeclaration());
            }
        }
    }

    private void printStreamDiffWarnings(Platform platform1, List<String> platform1Streams, Platform platform2, List<String> platform2Streams) {
        Set<String> onlyInPlatform1 = new HashSet<>(platform1Streams);
        onlyInPlatform1.removeAll(platform2Streams);

        Set<String> onlyInPlatform2 = new HashSet<>(platform2Streams);
        onlyInPlatform2.removeAll(platform1Streams);

        printStreamDiffWarnings(platform1, onlyInPlatform1);
        printStreamDiffWarnings(platform2, onlyInPlatform2);
    }

    private void printStreamDiffWarnings(Platform platform, Set<String> streams) {
        if (streams.size() > 0) {
            logger.warn(streams.size() + " streams only available in " + platform.getName() + ":");
            for (String stream : streams) {
                logger.warn("* " + stream);
            }
        }
    }
}
