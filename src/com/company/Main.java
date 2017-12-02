package com.company;

import fastily.jwiki.core.Wiki;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.lambda3.graphene.core.Graphene;
import org.lambda3.text.simplification.discourse.model.Element;
import org.lambda3.text.simplification.discourse.model.SimpleContext;
import org.lambda3.text.simplification.discourse.model.SimplificationContent;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    private static String[][] targetWords = { {"Car","automobile"}, {"Gem", "jewel"}, {"Journey", "voyage"}, {"Boy", "lad"},
            {"Coast", "shore"}, {"Asylum", "madhouse"}, {"Magician", "wizard"}, {"Midday", "noon"}, {"Furnace", "stove"},
            {"Food", "fruit"}, {"Bird", "cock"}, {"Bird", "crane"}, {"Tool", "implement"}, {"Brother", "monk"}, {"Lad", "brother"},
            {"Crane", "implement"}, {"Journey", "car"}, {"Monk", "oracle"}, {"Cemetery", "woodland"}, {"Food", "rooster"},
            {"Coast", "hill"}, {"Forest", "graveyard"}, {"Shore", "woodland"}, {"Monk", "slave"}, {"Coast", "forest"},
            {"Lad", "wizard"}, {"Chord", "smile"}, {"Glass", "magician"}, {"Rooster", "voyage"}, {"Noon", "string"} };

    public static void main(String[] args) throws Exception{
        String strSC = null;
        int hitsPerPage = 10000;
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        MySimilarity similarity = new MySimilarity();
        IndexWriter w = new IndexWriter(index, config);
        config.setSimilarity(similarity);

        Graphene graphene = new Graphene();

        for(int tarWordCnt=0; tarWordCnt < targetWords.length; tarWordCnt++ ){
            System.out.println("Counter: " + tarWordCnt);
            SimplificationContent sc = graphene.doDiscourseSimplification(wikiInfo.getMyWikiArticlesByWord(targetWords[tarWordCnt][0]), false, false);
            simpleContexToLucene(w, sc);
        }
        w.commit();

        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("|                                  Results                                     |");
        System.out.println("+------------------------------------------------------------------------------+");


        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);

        for(int tarWordCnt=0; tarWordCnt < targetWords.length; tarWordCnt++ ) {
            Query q = new QueryParser("text", analyzer).parse(targetWords[tarWordCnt][1]);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            System.out.println("    Pair:   " +  "\t" + targetWords[tarWordCnt][0].toLowerCase() + " - " +  targetWords[tarWordCnt][1] + "\n");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d  = searcher.doc(docId);
                System.out.println("\t\t"+ hits[i].score + " : " +  d.get("text"));
            }
        }
        reader.close();
    }

    public static void simpleContexToLucene(IndexWriter w, SimplificationContent sc) {
        List<Element> listOfElements = sc.getElements();
        List<SimpleContext> tempList = null;
        Document doc = new Document();

        for(int elementCounter = 0; elementCounter < listOfElements.size(); elementCounter++){
            tempList = listOfElements.get(elementCounter).getSimpleContexts();
            for(int sContextCounter = 0; sContextCounter < tempList.size(); sContextCounter++){
                doc.add(new TextField("classification", tempList.get(sContextCounter).getRelation().name(), Field.Store.YES));
                doc.add(new TextField("text", tempList.get(sContextCounter).getText(), Field.Store.YES));
                System.out.println("classification: " + tempList.get(sContextCounter).getRelation().name());
                System.out.println("text: " + tempList.get(sContextCounter).getText());
            }
        }

        try {
            w.addDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


