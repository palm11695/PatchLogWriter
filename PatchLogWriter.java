import info.debatty.java.stringsimilarity.Jaccard;

import java.io.*;
import java.util.*;

public class PatchLogWriter {

    public static Map<String,String[]> filteredData = new TreeMap<>();
//    public static Map<String,String[]> cropFilteredData = new TreeMap<>();
    public static ArrayList<String> similarity = new ArrayList<>();
    public static ArrayList<String> simWithAuthorLabel = new ArrayList<>();
    public static Map<String, Double[]> metrics = new LinkedHashMap<>();
    public static ArrayList<String> simWithVector = new ArrayList<>();

    public static void processData(String fileName) {

        String line;

        ArrayList<String[]> dataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while((line = br.readLine()) != null) {
                String[] separateData = line.split(",");
                dataList.add(separateData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataList.remove(0);


//        ArrayList<String> cropKeySet = new ArrayList<>();
//        int keyCounter = 0;

        for(String[] data : dataList) {

            boolean isMerged = data[4].equals("MERGED");

            if(isMerged) {
                filteredData.put(data[1], data);

//                if(keyCounter<200) {
//                    cropKeySet.add(data[1]);
//                }
//                keyCounter++;
            }

        }

//        for(String key : cropKeySet) {
//            cropFilteredData.put(key, filteredData.get(key));
//        }


        // X

//        try {
//            Runtime.getRuntime().exec("mkdir diff-log");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        for(String key : filteredData.keySet()) {
//
//            String[] data = filteredData.get(key);
//
//            String absolutePath = new File("./").getAbsolutePath();
//            absolutePath = absolutePath.substring(0, absolutePath.length()-1);
//
//            String dir = absolutePath + "diff-log/";
//            String exportedFileName = data[1] + ".txt";
//
//            String command = "git log -p -1 " + data[8];
//
//            try {
//
//                boolean isFileCreated = new File(dir + exportedFileName).createNewFile();
//
//                if(isFileCreated) {
//
//                    FileWriter fw = new FileWriter(dir + exportedFileName);
//
//                    Process proc = Runtime.getRuntime().exec(command);
//                    BufferedReader cmdBr = new BufferedReader(new InputStreamReader((proc.getInputStream())));
//
//                    boolean haveToWrite = true;
//                    boolean isFirstLine = true;
//                    boolean wroteOnJavaFile = false;
//
//                    while ((line = cmdBr.readLine()) != null) {
//
//                        //temp : write only author
//                        /*
//                        if(line.startsWith("Author:")) {
//                            for(char c : line.toCharArray()) {
//                                if(c == '<') {
//                                    break;
//                                }
//                                fw.write(c);
//                            }
//                            break;
//                        }
//                        */
//
//
//                        if(line.length() > 0) {
//
//                            boolean isTripleOps = line.startsWith("+++") || line.startsWith("---");
//                            boolean isComment_1 = line.replace(" ", "").startsWith("+/") || line.replace("\t", "").startsWith("+/");
//                            boolean isComment_2 = line.replace(" ", "").startsWith("+*") || line.replace("\t","").startsWith("+*");
////                            boolean isComment_3 = line.replace(" ", "").startsWith("-/") || line.replace("\t","").startsWith("-/");
////                            boolean isComment_4 = line.replace(" ", "").startsWith("-*") || line.replace("\t","").startsWith("-*");
//                            boolean isModCode = (line.charAt(0) == '+' /* || line.charAt(0) == '-'*/) && !isTripleOps && !isComment_1 && !isComment_2 /* && !isComment_3 && !isComment_4 */;
//                            boolean isOnlyOp = line.replace(" ", "").equals("+") /* || line.replace(" ", "").equals("-") */;
//                            boolean isGitDiff = line.startsWith("diff --git");
//                            boolean isJavaDiff = line.contains(".java");
//
//                            if(isGitDiff && isJavaDiff) {
//
//                                if(!isFirstLine && wroteOnJavaFile) {
//                                    fw.write("\n\n\n");
//                                }
//                                haveToWrite = true;
//                                wroteOnJavaFile = false;
//                            }
//                            else if(isGitDiff) {
//
//                                if(!isFirstLine && wroteOnJavaFile) {
//                                    fw.write("\n\n\n");
//                                }
//                                haveToWrite = false;
//                                wroteOnJavaFile = false;
//                            }
//
//                            if(haveToWrite && isModCode && !isOnlyOp) {
//                                fw.write(line.substring(1) + "\n");
////                                if(line.contains("\t")) {
////                                    System.out.println("[Tab]");
////                                    System.out.println(line);
////                                }
//                                wroteOnJavaFile = true;
//                                if(isFirstLine) {
//                                    isFirstLine = false;
//                                }
//                            }
//                        }
//
//                    }
//                    fw.close();
//                    System.out.println(exportedFileName + " has been created successfully.");
//                }
//                else {
//                    System.out.println("Error occurs with " + exportedFileName);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }

        // X
    }

    public static void findAuthor() {

        String line;
        String absolutePath = new File("./").getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.length()-1);

        String exportedFileName = "authorMatching.csv";

        try {
            boolean isFileCreated = new File(absolutePath + exportedFileName).createNewFile();
            if(isFileCreated) {
               FileWriter fw = new FileWriter(absolutePath + exportedFileName);
               fw.write("file_a,file_b,label\n");

                for(String key : filteredData.keySet()) {

                    int reviewNumber = Integer.parseInt(key);
                    String[] data = filteredData.get(key);
                    String command = "git log -p -1 " + data[8];
                    String author = "a";
                    StringBuilder authorBuilder = new StringBuilder();

                    Process proc = Runtime.getRuntime().exec(command);
                    BufferedReader cmdBr = new BufferedReader(new InputStreamReader((proc.getInputStream())));

                    while((line = cmdBr.readLine()) != null) {
                        if(line.startsWith("Author:")) {
                            for(char c : line.substring(8).toCharArray()) {
                                authorBuilder.append(c);
                                if(c == '<') {
                                    author = authorBuilder.substring(0, authorBuilder.length()-1);
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    for(String otherKey : filteredData.keySet()) {

                        int otherReviewNumber = Integer.parseInt(otherKey);

                        if(reviewNumber < otherReviewNumber) {
                            String[] otherData = filteredData.get(key);
                            String otherCommand = "git log -p -1 " + otherData[8];
                            String otherAuthor = "b";
                            StringBuilder otherAuthorBuilder = new StringBuilder();

                            Process otherProc = Runtime.getRuntime().exec(otherCommand);
                            BufferedReader otherCmdBr = new BufferedReader(new InputStreamReader((otherProc.getInputStream())));

                            while((line = otherCmdBr.readLine()) != null) {
                                if(line.startsWith("Author:")) {
                                    for(char c : line.substring(8).toCharArray()) {
                                        otherAuthorBuilder.append(c);
                                        if(c == '<') {
                                            otherAuthor = otherAuthorBuilder.substring(0, otherAuthorBuilder.length()-1);
                                            break;
                                        }
                                    }
                                }
                            }

                            if(author.equals(otherAuthor)) {
                                fw.write(key + "," + otherKey + ",1\n");
                            }
                            else {
                                fw.write(key + "," + otherKey + ",0\n");
                            }
                        }

                    }

                }
                fw.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void findSimilarity() {

        Jaccard jaccard = new Jaccard(2);
        int i = 0;

        //Temp
        String dir = "/Users/palm11695/IdeaProjects/DiffGenerator/diff-log/";
        String authorDir = "/Users/palm11695/IdeaProjects/DiffGenerator/diff-log-author/";

        for(String key : filteredData.keySet()) {

            String line;

            int reviewNumber = Integer.parseInt(key);
            String file = dir + key + ".txt";
            StringBuilder data = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                while((line = br.readLine()) != null) {
                    data.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(data.length() > 0) {

                for(String otherKey : filteredData.keySet()) {

                    int otherReviewNumber = Integer.parseInt(otherKey);

                    if(reviewNumber < otherReviewNumber) {

                        int label = 0;
                        String otherFile = dir + otherKey + ".txt";
                        StringBuilder otherData = new StringBuilder();

                        try (BufferedReader br = new BufferedReader(new FileReader(otherFile))) {

                            while((line = br.readLine()) != null) {
                                otherData.append(line);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(otherData.length() > 0) {

                            double sim = jaccard.similarity(data.toString(), otherData.toString());

                            String author = null;
                            String otherAuthor = null;

                            try (BufferedReader br = new BufferedReader(new FileReader(authorDir + key + ".txt"))) {
                                author = br.readLine();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try (BufferedReader br = new BufferedReader(new FileReader(authorDir + otherKey + ".txt"))) {
                                otherAuthor = br.readLine();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if(author != null && otherAuthor != null) {
                                if(author.equals(otherAuthor)) {
                                    label = 1;
                                }
                            }

                            similarity.add(key + "," + otherKey + "," + sim + "," + label);
                            System.out.println(similarity.get(i));
                            i++;
                        }
                    }
                }

            }
//            System.out.println("Computing");

        }

        System.out.println("Similarity computing has been finished.");

        for(String eachRow : similarity) {
            String[] data = eachRow.split(",");
            Double[] vectorA = metrics.get(data[0]);
            Double[] vectorB = metrics.get(data[1]);
            StringBuilder newData = new StringBuilder();
            newData.append(data[0]).append(",").append(data[1]).append(",").append(data[2]).append(",");
            for(Double each : vectorA) {
                newData.append(each).append(",");
            }
            for(Double each : vectorB) {
                newData.append(each).append(",");
            }
            newData.append(data[3]);
            simWithVector.add(newData.toString());
        }

        // Write CSV file

        try {

            boolean isFileCreated = new File("results.csv").createNewFile();

            if(isFileCreated) {

                FileWriter fw = new FileWriter("results.csv");

                fw.write("changeId1,changeId2,jaccard_sim,feat1_a,feat2_a,feat3_a,feat4_a,feat5_a,feat6_a,feat1_b,feat2_b,feat3_b,feat4_b,feat5_b,feat6_b,label\n");
                for(String eachRow : simWithVector) {
                    fw.write(eachRow + "\n");
                }
                fw.close();
                System.out.println("results.csv has been created successfully.");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void findAuthorLocal() {

        //Temp
        String dir = "/Users/palm11695/IdeaProjects/DiffGenerator/diff-log-author/";

        try {

            boolean isFileCreated = new File("authorMatching.csv").createNewFile();

            if(isFileCreated) {

                FileWriter fw = new FileWriter("authorMatching.csv");

                fw.write("file_a,file_b,label\n");

                for(String key : filteredData.keySet()) {

                    int reviewNumber = Integer.parseInt(key);
                    String file = dir + key + ".txt";

                    String author = null;
                    String otherAuthor = null;

                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                        author = br.readLine();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for(String otherKey : filteredData.keySet()) {

                        int otherReviewNumber = Integer.parseInt(otherKey);

                        if(reviewNumber < otherReviewNumber) {

                            String otherFile = dir + otherKey + ".txt";

                            try (BufferedReader br = new BufferedReader(new FileReader(otherFile))) {

                                otherAuthor = br.readLine();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if(author != null && otherAuthor != null) {

                            if(author.equals(otherAuthor)) {
                                fw.write(key + "," + otherKey + ",1\n");
                                System.out.println(author + "| " + otherAuthor);
                            }
                            else {
                                fw.write(key + "," + otherKey + ",0\n");
                            }
                        }

                    }


                    System.out.println("Comparing...");

                }

                fw.close();

                System.out.println("authorMatching.csv has been created successfully.");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void layoutFeature() {

        String line;

        String absolutePath = new File("./").getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.length()-1);
        String dir = absolutePath + "diff-log/";

        Map<String, String> haveData = new LinkedHashMap<>();
        Map<String, Integer> numEmptyLinesList = new LinkedHashMap<>();

        for(String key : filteredData.keySet()) {

            StringBuilder data = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new FileReader(dir + key + ".txt"))) {

                Integer numEmptyLines = 0;

                while((line = br.readLine()) != null) {

                    data.append(line).append("\n");

                    if(!(line.length() > 1) && !line.contains("}")) {
                        numEmptyLines++;
                    }
                }

                if(data.length() > 1) {
                    haveData.put(key, data.toString());
                    numEmptyLinesList.put(key, numEmptyLines);
                }
//                else {
//                    System.out.println(key + ".txt is an empty file.");
//                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        for(String key : haveData.keySet()) {

            double numTabs = 0;
            double numSpaces = 0;
            double numEmptyLines = numEmptyLinesList.get(key);
            double numWhiteSpaces = 0;
            double newLineBeforeOpenBrace = 0;
            double tabsLeadLines = 0;

            String allCode = haveData.get(key);
            double fileLength = allCode.length();

            int conSpaces = 0;
            double numSpaceTabs = 0;

            double numLines = 0;

            for(char c : allCode.toCharArray()) {

                if(c == ' ') {
                    numSpaces += 1;
                    conSpaces++;
                }
                else {
                    conSpaces = 0;
                }

                if(c == '\t') {
                    numTabs += 1;
                }
                if(c == '\n') {
                    numLines += 1;
                }

                //Assume 4 continue spaces = 1 tab
                if(conSpaces == 4) {
                    numSpaceTabs += 1;
                    conSpaces = 0;
                }
            }

            numTabs += numSpaceTabs;
            numSpaces -= (numSpaceTabs * 4);

            numWhiteSpaces = numSpaces + numTabs + numLines;

            if(allCode.contains("\n" + " ") || allCode.contains("\n" + "\t")) {
                tabsLeadLines = 1;
            }

            if(allCode.replace(" ", "").contains("\n{") || allCode.replace("\t", "").contains("\n{")) {
                newLineBeforeOpenBrace = 1;
            }

            //
            if(numTabs == 0) {
                numTabs = -1;
            }
            if(numSpaces == 0) {
                numSpaces = -1;
            }
            if(numEmptyLines == 0) {
                numEmptyLines = -1;
            }
            if(numWhiteSpaces == 0) {
                numWhiteSpaces = -1;
            }
            if(numLines == 0) {
                numLines = -1;
            }
            //

            // ln(numTabs/length), ln(numSpaces/length), *ln(numLines/length)*, whiteSpacesRatio, newLineBeforeOpenBrace, tabsLeadLine
            metrics.put(key, new Double[]{Math.log(numTabs/fileLength), Math.log(numSpaces/fileLength), Math.log(numLines/fileLength), numWhiteSpaces/(fileLength-numWhiteSpaces), newLineBeforeOpenBrace, tabsLeadLines});

        }

//        for(String key : metrics.keySet()) {
//            System.out.println(key + " >> " + Arrays.toString(metrics.get(key)));
//        }

    }

    public static void mergeSimWithAuthor() {

        String line;
        String dir = "/Users/palm11695/IdeaProjects/DiffGenerator/";

        ArrayList<String[]> authorMatching = new ArrayList<>();
        ArrayList<String[]> results = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(dir + "authorMatching.csv"))) {

            while((line = br.readLine()) != null) {
                authorMatching.add(line.split(","));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Read authorMatching.csv : done.");

        authorMatching.remove(0);

        try (BufferedReader br = new BufferedReader(new FileReader(dir + "results.csv"))) {

            while((line = br.readLine()) != null) {
                results.add(line.split(","));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Read results.csv : done.");

        results.remove(0);


        for(String[] a : authorMatching) {

            for(String[] b : results) {

                if(a[0].equals(b[0]) && a[1].equals(b[1])) {
                    simWithAuthorLabel.add(b[0] + "," + b[1] + "," + b[2] + "," + a[2]);
                }
            }

            System.out.println("Merging...");

        }

        // Write CSV file

        try {

            boolean isFileCreated = new File("resultsWithAuthor.csv").createNewFile();

            if(isFileCreated) {

                FileWriter fw = new FileWriter("resultsWithAuthor.csv");

                fw.write("changeId1,changeId2,jaccard_sim,label\n");
                for(String eachRow : simWithAuthorLabel) {
                    fw.write(eachRow + "\n");
                }
                fw.close();
                System.out.println("resultsWithAuthor.csv has been created successfully.");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void confusionMatrix() {

        String line;
        String file = "/Users/palm11695/IdeaProjects/DiffGenerator/results (2)Filter-testset.csv";
        ArrayList<ArrayList<String>> testSet = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {

            while((line = br.readLine()) != null) {
                testSet.add(new ArrayList<>(Arrays.asList(line.split(","))));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        testSet.remove(0);

        System.out.println("Read results (2)Filter-testset.csv : done.");

        for(ArrayList<String> eachRow : testSet) {
            if(Double.parseDouble(eachRow.get(12)) >= 0.5) {
                eachRow.add("1");
            }
            else {
                eachRow.add("0");
            }

            int label = Integer.parseInt(eachRow.get(13));
            int predicted = Integer.parseInt(eachRow.get(14));

            if(label == 1 && predicted == 1) {
                eachRow.add("TP");
            }
            else if(label == 1 && predicted == 0) {
                eachRow.add("FN");
            }
            else if(label == 0 && predicted == 1) {
                eachRow.add("FP");
            }
            else if(label == 0 && predicted == 0) {
                eachRow.add("TN");
            }

            System.out.println(eachRow);
        }

        // Write CSV file

        try {

            boolean isFileCreated = new File("testSetWithCM_05.csv").createNewFile();

            if(isFileCreated) {

                FileWriter fw = new FileWriter("testSetWithCM_05.csv");

                fw.write("feat1_a,feat2_a,feat3_a,feat4_a,feat5_a,feat6_a,feat1_b,feat2_b,feat3_b,feat4_b,feat5_b,feat6_b,jaccard_sim,label,predicted,confusion_matrix\n");
                for(ArrayList<String> eachRow : testSet) {
                    for(int i=0; i<15; i++) {
                        fw.write(eachRow.get(i) + ",");
                    }
                    fw.write(eachRow.get(15) + "\n");
                }
                fw.close();
                System.out.println("testSetWithCM_05.csv has been created successfully.");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writePerformance() {

        String line;
        String file = "/Users/palm11695/IdeaProjects/DiffGenerator/testSetWithCM_05.csv";
        ArrayList<String[]> resultWithCM = new ArrayList<>();
        double TP = 0, FN = 0, FP = 0, TN = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while((line = br.readLine()) != null) {
                resultWithCM.add(line.split(","));
            }

        } catch (Exception e) {
            e.printStackTrace();
        };
        
        for(String[] eachRow : resultWithCM) {

            switch (eachRow[15]) {
                case "TP":
                    TP += 1;
                    break;
                case "FN":
                    FN += 1;
                    break;
                case "FP":
                    FP += 1;
                    break;
                case "TN":
                    TN += 1;
                    break;
            }
        }

        System.out.println("Count : " + TP + ", " + FN + ", " + FP + ", " + TN);

        double precision = TP / (TP + FP);
        double recall = TP / (TP + FN);
        double F1 = (2 * (precision * recall))/(precision + recall);

        System.out.println("Precision : " + precision);
        System.out.println("Recall : " + recall);
        System.out.println("F1 Score : " + F1);
    }
    public static void main(String[] args) {

//        processData(args[0]);
//        findAuthor();

//        processData("/Users/palm11695/Documents/GitHub/NCDSearch/CROP Dataset/metadata/couchbase-java-client.csv");
//        System.out.println("Metadata process successfully.");
//        findAuthorLocal();
//        layoutFeature();
//        mergeSimWithAuthor();

//        findSimilarity();

        confusionMatrix();
        writePerformance();

    }
}
