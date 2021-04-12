package pt.fcul.lasige.sonaar.data;

public class Counter {
    int post;
    int feed;

    public Counter(int post, int feed) {
        this.post = post;
        this.feed = feed;
    }

    public void incPost(){
        post++;
    }

    public void incFeed(){
        feed++;
    }

    public int getPost() {
        return post;
    }

    public int getFeed() {
        return feed;
    }

}
