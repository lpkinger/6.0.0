		Ext.define('erp.view.common.multiDbfind.Toolbar', {
		    extend: 'Ext.toolbar.Paging',
		    alias: 'widget.erpMultiDbfindToolbar',
		    doRefresh:function(){
		    	window.location = basePath + "common/multidbfind.jsp?whoami=" + caller + "&condition=" + condition;
		    },
		    updateInfo : function(){
	 	    	 var page=this.child('#inputItem').getValue();
	                var me = this,
	                    displayItem = me.child('#displayItem'),
	                    //store = me.store,
	                    pageData = me.getPageData();
	                    pageData.fromRecord=(page-1)*pageSize+1;
		    			pageData.toRecord=page*pageSize;
		    			pageData.total=count;
	                    count, msg;
	              
	                if (displayItem) {
	                    if (count === 0) {
	                        msg = me.emptyMsg;
	                    } else {
	                        msg = Ext.String.format(
	                            me.displayMsg,
	                            pageData.fromRecord,
	                            pageData.toRecord,
	                            pageData.total
	                        );
	                    }
	                    displayItem.setText(msg);
	                    me.doComponentLayout();
	                }
	            },
	            getPageData : function(){
		        	var store = this.store,
		        	   totalCount = store.getTotalCount();
		        	   totalCount=count;
		        	  
		        	return {
		        		total : totalCount,
		        		currentPage : store.currentPage,
		        		pageCount: Math.ceil(totalCount / store.pageSize),
		        		fromRecord: ((store.currentPage - 1) * store.pageSize) + 1,
		        		toRecord: Math.min(store.currentPage * store.pageSize, totalCount)
		        	};
		            
		        },
		        onPagingKeyDown : function(field, e){
		            var me = this,
		                k = e.getKey(),
		                pageData = me.getPageData(),
		                increment = e.shiftKey ? 10 : 1,
		                pageNum = 0;

		            if (k == e.RETURN) {
		                e.stopEvent();
		                pageNum = me.readPageFromInput(pageData);
		                if (pageNum !== false) {
		                    pageNum = Math.min(Math.max(1, pageNum), pageData.pageCount);
		                    me.child('#inputItem').setValue(pageNum);
		                    if(me.fireEvent('beforechange', me, pageNum) !== false){
		                    	page = pageNum;
		                    	Ext.getCmp("dbfindGridPanel").getColumnsAndStore();
		                    }
		                    
		                }
		            } else if (k == e.HOME || k == e.END) {
		                e.stopEvent();
		                pageNum = k == e.HOME ? 1 : pageData.pageCount;
		                field.setValue(pageNum);
		            } else if (k == e.UP || k == e.PAGEUP || k == e.DOWN || k == e.PAGEDOWN) {
		                e.stopEvent();
		                pageNum = me.readPageFromInput(pageData);
		                if (pageNum) {
		                    if (k == e.DOWN || k == e.PAGEDOWN) {
		                        increment *= -1;
		                    }
		                    pageNum += increment;
		                    if (pageNum >= 1 && pageNum <= pageData.pages) {
		                        field.setValue(pageNum);
		                    }
		                }
		            }
		            me.updateInfo();
	                fn(me,pageNum);
		        }, 
		        moveFirst : function(){
	            	var me = this;
	                me.child('#inputItem').setValue(1);
	                value=1;
	            	page = value;
	            	Ext.getCmp("dbfindGridPanel").getColumnsAndStore();
	                me.updateInfo();
	            	fn(me,value);
	            },
	            movePrevious : function(){
	                var me = this;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue()-1);
	                value=me.child('#inputItem').getValue();
	            	page = value;
	            	Ext.getCmp("dbfindGridPanel").getColumnsAndStore();
	                me.updateInfo();
	                fn(me,value);
	              
	            },
	            moveNext : function(){
	                var me = this,
	                last = me.getPageData().pageCount;
	                total=last;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue()+1);
	                value=me.child('#inputItem').getValue();
	            	page = value;
	            	Ext.getCmp("dbfindGridPanel").getColumnsAndStore();
	                me.updateInfo();
	                fn(me,value);
	            },
	            moveLast : function(){
	                var me = this,
	                last = me.getPageData().pageCount;
	                total=last;
	                me.child('#inputItem').setValue(last);
	                value=me.child('#inputItem').getValue();
	            	page = value;
	            	Ext.getCmp("dbfindGridPanel").getColumnsAndStore();
	                me.updateInfo();
	                fn(me,value);
	            }
		});
		function fn(me,value){
			me.child('#last').setDisabled(value==total);
		    me.child('#next').setDisabled(value==total);
		    me.child('#first').setDisabled(value<=1);
		    me.child('#prev').setDisabled(value<=1);
			
		}