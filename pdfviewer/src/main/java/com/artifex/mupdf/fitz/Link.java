package com.artifex.mupdf.fitz;

public class Link {
   public Rect bounds;
   public int page;
   public String uri;

   public Link(Rect bounds, int page, String uri) {
      this.bounds = bounds;
      this.page = page;
      this.uri = uri;
   }

   public String toString() {
      return "Link(b=" + this.bounds + ",page=" + this.page + ",uri=" + this.uri + ")";
   }
}
