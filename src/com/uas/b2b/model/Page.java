package com.uas.b2b.model;

import java.util.List;


public class Page<T> {
		private static final long serialVersionUID = 1L;
		private List<T> content;
		private boolean first;
		private boolean last;
		private int page;
		private int size;
		private long totalElement;
		private long totalPage;
		public Page() {
		}
		public Page(List<T> content, boolean first, boolean last, int page, int size, long totalElement,
				long totalPage) {
			super();
			this.content = content;
			this.first = first;
			this.last = last;
			this.page = page;
			this.size = size;
			this.totalElement = totalElement;
			this.totalPage = totalPage;
		}
		public List<T> getContent() {
			return content;
		}
		public void setContent(List<T> content) {
			this.content = content;
		}
		public boolean isFirst() {
			return first;
		}
		public void setFirst(boolean first) {
			this.first = first;
		}
		public boolean isLast() {
			return last;
		}
		public void setLast(boolean last) {
			this.last = last;
		}
		public int getPage() {
			return page;
		}
		public void setPage(int page) {
			this.page = page;
		}
		public int getSize() {
			return size;
		}
		public void setSize(int size) {
			this.size = size;
		}
		public long getTotalElement() {
			return totalElement;
		}
		public void setTotalElement(long totalElement) {
			this.totalElement = totalElement;
		}
		public long getTotalPage() {
			return totalPage;
		}
		public void setTotalPage(long totalPage) {
			this.totalPage = totalPage;
		}
		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		
}
