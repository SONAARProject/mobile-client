package pt.fcul.lasige.sonaar.data;

public class Counter {
    int post;
    int feed;
    int altText;

    public Counter(int post, int feed, int altText) {
        this.post = post;
        this.feed = feed;
        this.altText = altText;
    }

    public void incPost(){
        post++;
    }

    public void incFeed(){
        feed++;
    }

    public void incAltText(){
        altText++;
    }

    public int getPost() {
        return post;
    }

    public int getFeed() {
        return feed;
    }

    public int getAltText() {
        return altText;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "post=" + post +
                ", feed=" + feed +
                ", altText=" + altText +
                '}';
    }
}
