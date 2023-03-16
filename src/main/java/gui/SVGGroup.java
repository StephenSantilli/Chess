package gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

public class SVGGroup extends SVGElement {

    private ArrayList<SVGElement> childTags;
    private ArrayList<SVGGroup> childGroups;

    private String transform;
    private String style;

    private double scale;

    public ArrayList<SVGElement> getChildTags() {
        return childTags;
    }

    public ArrayList<SVGGroup> getChildGroups() {
        return childGroups;
    }

    public String getTransform() {
        return transform;
    }

    public String getStyle() {
        return style;
    }

    public String getContent() {
        return content;
    }

    private String content;

    private static final String GROUP_REGEX = "(<g(( transform=\"(?<transform>[^\"]*)\")|( style=\"(?<style>[^\"]*)\")|( [^>]*))*>)";
    private static final String GROUP_END_REGEX = "</g>";
    private static final String PATH_REGEX = "(?<path>(<path(( style=\"(?<style>[^\"]*))|( d=\"(?<line>[^\"]*))|[^>])*>))|(?<circle>(<circle ?cx=\"(?<cx>[^\"]*)\" ?cy=\"(?<cy>[^\"]*)\" ?r=\"(?<r>[^\"]*)\"[^>]*>))|(?<grp><g(( transform=\"(?<transform>[^\"]*)\")|( style=\"(?<gstyle>[^\"]*)\")|( [^>]*))*>)";
    private static final String CIRCLE_REGEX = "<circle ?cx=\"(?<cx>[^\"]*)\" ?cy=\"(?<cy>[^\"]*)\" ?r=\"(?<r>[^\"]*)\"[^>]*>";

    public SVGGroup(String groupContent, String groupStyle, String groupTransform, double scale) {

        this.transform = groupTransform;
        this.style = groupStyle;
        this.content = groupContent;
        this.scale = scale;

        this.childGroups = new ArrayList<SVGGroup>();
        this.childTags = new ArrayList<SVGElement>();

        Pattern pat = Pattern.compile(GROUP_REGEX);
        Pattern patend = Pattern.compile(GROUP_END_REGEX);

        String pathContent = content + "";

        Pattern pPat = Pattern.compile(PATH_REGEX);
        Matcher pm = pPat.matcher(pathContent);

        int startI = 0;
        while (pm.find(startI)) {

            if (pm.group("grp") != null && !pm.group("grp").equals("")) {
                
                Matcher m = pat.matcher(pathContent);
                Matcher em = patend.matcher(pathContent);

                ArrayList<EntryMatch> matches = new ArrayList<EntryMatch>();
                if (m.find(pm.start())) {
                    matches.add(new EntryMatch(true, m.start()));

                    while (m.find()) {

                        matches.add(new EntryMatch(true, m.start()));

                    }
                }

                if (em.find(pm.start())) {
                    matches.add(new EntryMatch(false, em.end()));

                    while (em.find()) {

                        matches.add(new EntryMatch(false, em.end()));

                    }
                }
                matches.sort((a, b) -> a.getIndex() - b.getIndex());
                int nests = 0;
                int nestStart = -1;
                int nestEnd = -1;
                ArrayList<MatchPair> pairs = new ArrayList<MatchPair>();
                for (int i = 0; i < matches.size(); i++) {

                    if (matches.get(i).isStart())
                        ++nests;
                    else
                        --nests;

                    if (nests == 0) {
                        nestEnd = matches.get(i).getIndex();
                        pairs.add(new MatchPair(nestStart, nestEnd));
                        nestStart = -1;
                        nestEnd = -1;
                    } else if (nests == 1 && matches.get(i).isStart())
                        nestStart = matches.get(i).getIndex();

                }

                for (int i = 0; i < pairs.size(); i++) {

                    int start = pairs.get(i).getStart(), end = pairs.get(i).getEnd();

                    String c = pathContent.substring(start, end);

                    Matcher ngm = pat.matcher(c);
                    ngm.find();
                    childTags.add(new SVGGroup(c.substring(ngm.end()), ngm.group("style"), ngm.group("transform"), scale));

                    // pathContent = pathContent.substring(0, start) + pathContent.substring(end -
                    // 4);
                    startI = end - 4;

                }

                // pPat = Pattern.compile(PATH_REGEX);
                // pm = pPat.matcher(pathContent);

            } else if (pm.group("path") != null && !pm.group("path").equals("")){
                childTags.add(new SVGPathTag(pathContent.substring(pm.start(), pm.end()), scale));
                startI = pm.end();
            }
            else if (pm.group("circle") != null && !pm.group("circle").equals("")){
                childTags.add(new SVGCircle(Double.parseDouble(pm.group("cx")), Double.parseDouble(pm.group("cy")),
                        Double.parseDouble(pm.group("r")), scale));
                startI = pm.end();
            }
        }

    }

    public ArrayList<Shape> draw(ArrayList<String> styles) {

        if (styles == null)
            styles = new ArrayList<String>();

        if (style != null)
            styles.add(style);

        ArrayList<Shape> paths = new ArrayList<Shape>();

        for (SVGElement path : childTags) {

            paths.addAll(path.draw(styles));

        }

        Pattern tPat = Pattern.compile("translate\\((?<x>[^\\,]*)\\, ?(?<y>[^\\)]*)\\)");
        if(transform != null && !transform.equals("")) {
            Matcher tm = tPat.matcher(transform);
            tm.find();
            double tx = Double.parseDouble(tm.group("x")), ty = Double.parseDouble(tm.group("y"));;

            for (Shape p : paths) {
                p.setTranslateX(tx * scale);
                p.setTranslateY(ty * scale);
            }

        }



        return paths;

    }

}
