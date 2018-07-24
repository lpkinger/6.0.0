		Ext.define('erp.view.scm.purchase.inquiryVastDatalist.Toolbar', {
		    extend: 'Ext.toolbar.Paging',
		    alias: 'widget.erpInquiryVastDatalistToolbar',
		    doRefresh:function(){
		    	this.moveFirst();
		    },
		    items: ['-',{
		    	xtype: 'erpVastDeleteButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastPostButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastCloseButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastReStartButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastFreezeButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastSendButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastAuditButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastSaveButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastSpareButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastSimulateButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastSubmitButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastWriexamButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastInterviewButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastJointalcpoolButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
		    },{
		    	xtype: 'erpVastWritemarkButton',
		    	cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpVastIntermarkButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpVastTurnfullmembButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpVastTurnoverButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpVastTurnCareeButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpTurnfullmembButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpTurnPositionButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpTurnCareeButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpVastSocailaccountButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpVastSocailsecuButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpVastSendOutButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpVastGetButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpAgreeToPriceButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpAgreeAllToPriceButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpNotAgreeToPriceButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},{
	    		xtype: 'erpSyncButton',
	    		cls: 'x-btn-gray',
		    	hidden: true
	    	},'-',{
	    		id:'datalistexport',
	    		name: 'export',
	    		text: '导出',
	    		iconCls: 'x-button-icon-excel',
	    	  	cls: 'x-btn-gray',
	    		width: 70,
	    		handler : function(i) {
	    			var me = i.ownerCt;
	    			me.exportData(me.ownerCt, i);
	    		}
	    	},{
		    	itemId: 'close',
		    	id:'closebutton',
		    	text: $I18N.common.button.erpCloseButton,
				iconCls: 'tree-delete',
				width: 70,
		    	cls: 'x-btn-gray',
		    	handler: function(btn){
					var main = parent.Ext.getCmp("content-panel");
					if(main)main.getActiveTab().close(); 
					else {parent.Ext.getCmp(parent.Ext.fly(window.frameElement).up('div.x-window').id).hide();}//win内嵌iframe
					
				}
		    }],
		    exportData : function(grid, btn, title, customFields) {
				if(!btn.locked) {
					var me = this;
					grid.BaseUtil.createExcel(caller, 'datalist', grid.getCondition(), title, null, customFields);
					if(dataCount > 6000) {
						me.insert(me.items.items.length - 2, {
							name: 'warn-tip',
							xtype: 'tbtext',
							text: '数据量较大，请耐心等待，不要重复点击。为减少服务器压力，单次导出上限为5万条.'
						});
						btn.setDisabled(true);
						btn.locked = true;
						setTimeout(function(){
							me.down('tbtext[name=warn-tip]').destroy();
							btn.setDisabled(false);
							btn.locked = false;
						}, 8000);
					}
				}
			},
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
		                    	Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
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
	            	Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
	                me.updateInfo();
	            	fn(me,value);
	            },
	            movePrevious : function(){
	                var me = this;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue()-1);
	                value=me.child('#inputItem').getValue();
	            	page = value;
	            	Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
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
	            	Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
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
	            	Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
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
					e.child("#next").setDisabled(b === c);
					e.child("#last").setDisabled(b === c);
					e.child("#refresh").enable();
					e.updateInfo();
					e.fireEvent("change", e, d);
				}
		});
		function fn(me,value){
			me.child('#last').setDisabled(value==total);
		    me.child('#next').setDisabled(value==total);
		    me.child('#first').setDisabled(value<=1);
		    me.child('#prev').setDisabled(value<=1);
			
		}