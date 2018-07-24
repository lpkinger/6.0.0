		Ext.define('erp.view.pm.mps.Toolbar', {
		    extend: 'Ext.toolbar.Paging',
		    alias: 'widget.erpMpsToolbar',
		    doRefresh:function(){
		    	//window.location = basePath + "jsps/common/datalist.jsp?whoami=" + caller + "&urlcondition=" + condition;
		    	//	var tab = Ext.getCmp('tabpanel');.getColumnsAndStore(caller, condition, 1, pageSize);
		    	this.moveFirst();
		    },
		    items: ['-',{
		    	id: 'erpAddButton',
		    	name: 'add',
		    	tooltip: $I18N.common.button.erpAddButton,
				iconCls: 'x-button-icon-add',
		    	width: 24,
		    	hidden: true,
		    	handler: function(btn){
		    		btn.ownerCt.ownerCt.BaseUtil.onAdd(caller, parent.Ext.getCmp('content-panel').getActiveTab().title, url);
		    	}
		    },{
		    	name: 'export',
		    	tooltip: $I18N.common.button.erpExportButton,
				iconCls: 'x-button-icon-excel',
		    	width: 24,
		    	handler: function(btn){
		    	   var grid = btn.ownerCt.ownerCt; 
		    	   if(grid.exportAction) {
		    		   grid.BaseUtil.customExport(caller, grid, grid.ownerCt.title, grid.exportAction, 
	    						grid.gridcondition);
		    	   } else {
		    		   grid.BaseUtil.createExcel(caller, 'datalist', grid.gridcondition);
		    	   }
		    	}
		    },'-',{
		    	itemId: 'close',
		    	//text: $I18N.common.button.erpCloseButton,
		    	tooltip:$I18N.common.button.erpCloseButton,
				iconCls: 'x-button-icon-close',
				width: 24,
		    	//cls: 'x-btn-gray-1',
		    	handler: function(){
					var main = parent.Ext.getCmp("content-panel"); 
					main.getActiveTab().close(); 
				}
		    }],
		    updateInfo : function(){
	 	    	 var page=this.child('#inputItem').getValue();
	                var me = this,
	                displayItem = me.child('#displayItem'),
                    //store = me.store,//update by yingp
                    pageData = me.getPageData();
                    pageData.fromRecord=(page-1)*pageSize+1;
	    			pageData.toRecord=page*pageSize > dataCount ? dataCount : page*pageSize;//
	    			pageData.total=dataCount;
/*	    			me.store.totalCount = dataCount;
	    			me.store.pageSize = pageSize;
	    			pageData.pageCount = Math.ceil(dataCount / pageSize);*/
                    dataCount, msg;
	                if (displayItem) {
	                    if (dataCount === 0) {
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
		        	   totalCount=dataCount;
		        	return {
		        		total : totalCount,
		        		currentPage : store.currentPage,
		        		pageCount: Math.ceil(dataCount / pageSize),
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
		                    	 var tab = Ext.getCmp('tabpanel');
		    	            var grid=tab.getActiveTab().items.items[0]; 
		                    	grid.getColumnsAndStore(caller, condition, page, pageSize,grid.id);
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
	               var tab = Ext.getCmp('tabpanel');
		    	   var grid=tab.getActiveTab().items.items[0]; 
	            	grid.getColumnsAndStore(caller, grid.gridcondition, page, pageSize,grid.id);
	                me.updateInfo();
	            	fn(me,value);
	            },
	            movePrevious : function(){
	                var me = this;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue()-1);
	                value=me.child('#inputItem').getValue();
	            	page = value;
	            	var tab = Ext.getCmp('tabpanel');
		    	   var grid=tab.getActiveTab().items.items[0]; 
	            	grid.getColumnsAndStore(caller, grid.gridcondition, page, pageSize,grid.id);
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
	            	var tab = Ext.getCmp('tabpanel');
		    	   var grid=tab.getActiveTab().items.items[0]; 
	            	grid.getColumnsAndStore(caller, grid.gridcondition, page, pageSize,grid.id);
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
	            	var tab = Ext.getCmp('tabpanel');
		    	   var grid=tab.getActiveTab().items.items[0]; 
	            	grid.getColumnsAndStore(caller, grid.gridcondition, page, pageSize,grid.id);
	                me.updateInfo();
	                fn(me,value);
	            },
	            onLoad : function() {
					var e = this, d, b, c, a;
					if (!e.rendered) {
						return
					}
					d = e.getPageData();
					b = d.currentPage;
					c = Math.ceil(dataCount / pageSize);
					a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
					e.child("#afterTextItem").setText(a);
					e.child("#inputItem").setValue(b);
					e.child("#first").setDisabled(b === 1);
					e.child("#prev").setDisabled(b === 1);
					e.child("#next").setDisabled(b === c || c===1);//
					e.child("#last").setDisabled(b === c || c===1);
					e.child("#refresh").enable();
					e.updateInfo();
					e.fireEvent("change", e, d);
				},
				afterOnLoad : function() {
					var e = this, d, c, a;
					if (!e.rendered) {
						return
					}
					d = e.getPageData();
					b = d.currentPage;
					c = Math.ceil(dataCount / pageSize);
					a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
					e.child("#afterTextItem").setText(a);
					e.updateInfo();
					e.fireEvent("change", e, d);
				    e.child('#last').setDisabled(c <= 1 || page == c);
				    e.child('#next').setDisabled(c <= 1 || page == c);
				}
		});
		function fn(me,value){
			me.child('#last').setDisabled(value==total);
		    me.child('#next').setDisabled(value==total);
		    me.child('#first').setDisabled(value<=1);
		    me.child('#prev').setDisabled(value<=1);
		}