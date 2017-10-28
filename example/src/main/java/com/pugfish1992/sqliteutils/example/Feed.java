package com.pugfish1992.sqliteutils.example;

/**
 * Created by daichi on 10/28/17.
 */

public class Feed {
    long id;
    String title;
    String subTitle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feed feed = (Feed) o;

        if (id != feed.id) return false;
        if (title != null ? !title.equals(feed.title) : feed.title != null) return false;
        return subTitle != null ? subTitle.equals(feed.subTitle) : feed.subTitle == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (subTitle != null ? subTitle.hashCode() : 0);
        return result;
    }
}
