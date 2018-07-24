Ext.define('erp.view.pm.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			xtype: 'tabpanel',
			//anchor: '100% 50%',
			id:'tab2',
			layout: 'fit',
			items :[{
				title:'本工作中心在制',
				layout:'border',
				id : 'center1',
				items : [{
					region: 'north',
					id : 'dealform',
					xtype: "erpBatchDealFormPanel",  
					height: 200,
				},{
					region: 'center',
					xtype: "erpBatchDealGridPanel"
				}]
			},{
		    	  tag : 'iframe',
		    	  frame : true,
		    	  title:'本工作中心移交',
		    	  anchor : '100% 100%',
		    	  layout : 'fit',
		    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/batchDeal.jsp?whoami=ThisCenterCommit" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}/*,{
				title:'本工作中心移交',
				layout:'border',
				id : 'center2',
				items :[{
					region: 'north',
					id : 'toolbar1',
					xtype: "toolbar",  
					height: 30,
					items : [{
						xtype : 'erpPCBatchPrintButton',
					},'-',{
						xtype : 'erpPCBatchCommitButton',
					}]
				}{
					region: 'north',
					id : 'dealform2',
					caller : 'wusy',
					xtype: "erpBatchDealFormPanel",  
					height: 200,
				},{
					region: 'center',
					xtype:'grid',
					id : 'ThisCenterCommit',
					autoScroll:true,
					region: 'center',
					columnLines : true,
					height : 200,
					columns : [],
					selModel: Ext.create('Ext.selection.CheckboxModel',{
				    	checkOnly : true,
						ignoreRightMouseSelection : false,
						listeners:{
					        selectionchange:function(selModel, selected, options){
					        	//selModel.view.ownerCt.summary(true);
					        	selModel.view.ownerCt.selectall = false;
					        }
					    },
					    getEditor: function(){
					    	return null;
					    },
					    onHeaderClick: function(headerCt, header, e) {
					        if (header.isCheckerHd) {
					            e.stopEvent();
					            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
					            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
					                this.deselectAll(true);
					            } else {
					                this.selectAll(true);
					                this.view.ownerCt.selectall = true;
					            }
					        }
					    }
					}),
				}]
			},*/,{
		    	  tag : 'iframe',
		    	  frame : true,
		    	  title:'前工作中心在制',
		    	  anchor : '100% 100%',
		    	  layout : 'fit',
		    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/batchDeal.jsp?whoami=BeforeCenterMake" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}/*{
				title:'前工作中心在制',
				layout:'border',
				id : 'center3',
				items :[{
					region: 'north',
					id : 'dealform3',
					xtype: "form",  
					height: 50,
				},{
					region: 'center',
					xtype:'grid',
					id : 'BeforeCenterMake',
					autoScroll:true,
					region: 'center',
					columnLines : true,
					//height : 200,
					columns : []
				}]
			}*/,{
		    	  tag : 'iframe',
		    	  frame : true,
		    	  title:'前工作中心提交',
		    	  anchor : '100% 100%',
		    	  layout : 'fit',
		    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/batchDeal.jsp?whoami=BeforeCenterCommit" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}/*{
				title:'前工作中心提交',
				layout:'border',//
				id : 'center4',
				items :[{
					region: 'north',
					id : 'toolbar2',
					xtype: "toolbar",  
					height: 30,
					items : [{
						xtype : 'erpPCBatchPostButton',
					}]
				},{
					region: 'center',
					xtype:'grid',
					id : 'BeforeCenterCommit',
					autoScroll:true,
					region: 'center',
					columnLines : true,
					//height : 200,
					columns : [],
					selModel: Ext.create('Ext.selection.CheckboxModel',{
				    	checkOnly : true,
						ignoreRightMouseSelection : false,
						listeners:{
					        selectionchange:function(selModel, selected, options){
					        	//selModel.view.ownerCt.summary(true);
					        	selModel.view.ownerCt.selectall = false;
					        }
					    },
					    getEditor: function(){
					    	return null;
					    },
					    onHeaderClick: function(headerCt, header, e) {
					        if (header.isCheckerHd) {
					            e.stopEvent();
					            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
					            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
					                this.deselectAll(true);
					            } else {
					                this.selectAll(true);
					                this.view.ownerCt.selectall = true;
					            }
					        }
					    }
					}),
				}]
			}*/,{
		    	  tag : 'iframe',
		    	  frame : true,
		    	  title:'后工作中心确认',
		    	  anchor : '100% 100%',
		    	  layout : 'fit',
		    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/batchDeal.jsp?whoami=AfterCenterConfirm" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}/*,{
				title:'后工作中心确认',
				layout:'border',
				id : 'center5',
				items :[{
					region: 'north',
					id : 'dealform5',
					xtype: "form",  
					height: 50,
				},{
					region: 'center',
					xtype:'grid',
					id : 'AfterCenterConfirm',
					autoScroll:true,
					region: 'center',
					columnLines : true,
					//height : 200,
					columns : []
				}]
			}*//*,{ region: 'center',
				layout : 'border',
				items : [{
					xtype:'grid',
					id : 'BeforeCenterCommit',
					autoScroll:true,
					region: 'center',
					columnLines : true,
					title : '前工作中心提交',
					height : 200,
					columns : []
				}]
			}*/]
			}
		        /*{
			region: 'north',
			id : 'dealform',
			xtype: "erpBatchDealFormPanel",  
			height: 200,
	    },{
			region: 'center',
			layout : 'border',
			items:[{
				region: 'center',
				layout : 'border',
				items : [{
					region: 'center',
					xtype: "erpBatchDealGridPanel"
				},{
					region: 'south',
					xtype : 'tabpanel',
					items : [{
						xtype:'grid',
						id : 'BeforeCenterCommit',
						autoScroll:true,
						region: 'center',
						columnLines : true,
						title : '前工作中心提交',
						height : 200,
						columns : []
					},{
						xtype:'grid',
						id : 'BeforeCenterMake',
						autoScroll:true,
						region: 'center',
						columnLines : true,
						title : '前工作中心在制',
						height : 200,
						columns : []
					},{
						xtype:'grid',
						id : 'AfterCenterConfirm',
						autoScroll:true,
						region: 'center',
						columnLines : true,
						title : '后工作中心待确认',
						height : 200,
						columns : []
					}]
					 	
				}]
				//anchor: '30% 75%',
			}]
	    }*/]
		});
		me.callParent(arguments); 
	}
});