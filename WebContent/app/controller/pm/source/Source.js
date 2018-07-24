Ext.QuickTips.init();
Ext.define('erp.controller.pm.source.Source', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.source.Source','pm.source.SourceGrid','core.button.Load',
    		'pm.source.SourceForm','core.button.LoadingSource','common.datalist.Toolbar','core.form.ConDateField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
    	],
    init:function(){
    var me=this;
    	this.control({    		
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(this);
    			}
    		},
    		/*'gridscroller':{
    			beforerender:function(scroller){
    				console.log(scroller.dock);
    				if(scroller.dock=="bottom"){
    					scroller.padding=" 0 0 0 100";
    					scroller.height=10;
    				}
    			}
    		},*/
    		'button[id=querybutton]':{
    			click:function(btn){
    			  var rang=Ext.getCmp('condate').value;
    			  if(rang&&rang!=null){
    			  if(caller.indexOf('ForeCast')>0){    				  
    				condition = "  ( sd_startdate "+rang+") AND ";
    			  }else condition = "( sd_delivery  "+rang+" ) AND ";
    			  }
    			  var grid=Ext.getCmp('grid');
    			  grid.getCount(caller,condition);
    			}
    		},
    		'combo[id=type]':{
    		    select:function(combo,records,eOpts){
                    var value=combo.value; 
		            var grid=Ext.getCmp('grid');          
                    var checked=Ext.getCmp('detail').checked;
                    if(checked){
                      caller=value.substring(0,value.length-1)+'D'; 
                     }else {
                      caller=value; 
                     }  
                    var rang=Ext.getCmp('condate').value;
      			    if(rang&&rang!=null){
      			      if(caller.indexOf('ForeCast')>0){    				  
      				   condition = "  ( sd_startdate "+rang+") AND ";
      			       }else condition = "( sd_delivery  "+rang+" ) AND ";
      			    }
                    grid.getCount(caller,condition);
    		    }  
    		 },
    		'condatefield[id=condate]':{
    			afterrender:function(date){
    				date.combo.value = 7;			
    				date.setDateFieldValue(7);
    				date.combo.setRawValue('自定义');   	
    			}
    		},
    		'checkbox':{
    			beforerender: function(f) {
					me.BaseUtil.getSetting('MpsDesk', 'mrpSeparateFactory', function(v) {
						f.setValue(v);
					});
				},
    			change:function(checkbox){
	      	       var grid=Ext.getCmp('grid');  
	    	       var type=Ext.getCmp('type').getValue();    	    
	    	       if(checkbox.checked){
		    	       if(type=='MRPSSaleM'){
		    	         caller='MRPSSaleD';  
		    	       }else {
		    	        caller='MRPSForeCastD'; 
		    	       }
	    	       } else{
	    	    	   caller=type; 
	    	       }
	    	       var rang=Ext.getCmp('condate').value;
	 			    if(rang&&rang!=null){
	 			      if(caller.indexOf('ForeCast')>0){    				  
	 				   condition = "  ( sd_startdate "+rang+") AND ";
	 			       }else condition = "( sd_delivery  "+rang+" ) AND ";
	 			    }
	               grid.getCount(caller,condition); 
	    		 }       		
    		},
    		'dbfindtrigger': {
    			change: function(trigger){
    				if(trigger.name == 'team_prjid'){
    					this.changeGrid(trigger);
    				}
    			}
    		},
    		'button[id=load]':{
		        click : function(btn) {
					var grid = Ext.getCmp('grid');
					var form = Ext.getCmp('sourceform');
					var records = grid.getMultiSelected();
					var detailcaller = '';
					if (caller == 'MRPSSaleM') {
						detailcaller = 'MRPSSaleD';
					} else if (caller == 'MRPSForeCastM') {
						detailcaller = 'MRPSForeCastD';
					}
					var keyField = "";
					if (caller == 'MRPSSaleM')
						keyField = 'sa_id';
					else if (caller == 'MRPSSaleD')
						keyField = 'sd_id';
					else if (caller == 'MRPSForeCastM')
						keyField = 'sf_id';
					else if (caller == 'MRPSForeCastD')
						keyField = 'sd_id';
					if (records.length > 0) {
						var Store = new Array();
						var gridStore = new Array();
						var dd;
						Ext.each(records, function(record, index) {
									dd = new Object();
									dd[keyField] = record.data[keyField];
									Store[index] = Ext.JSON.encode(dd);
								});
						var main = parent.Ext.getCmp("content-panel");
						var panel = parent.Ext.getCmp(getUrlParam("panelId"));
						var grid = panel.currentGrid;
						var items = grid.getStore().data.items;
						var count = 0;
						if (items.length > 0) {
							Ext.each(items, function(item, index) {
										if (item.data.md_prodcode != '') {
											gridStore[index] = Ext.JSON
													.encode(item.data);
											count++;
										}
									});
						}
						main.getActiveTab().setLoading(true);
						Ext.Ajax.request({
							url : basePath + 'pm/source/LoadSource.action',
							params : {
								keyValue : keyValue,
								mainCode : mainCode,
								caller : caller,
								detailcaller : detailcaller,
								Store : unescape(Store.toString().replace(
										/\\/g, "%")),
								gridStore : unescape(gridStore.toString()
										.replace(/\\/g, "%")),
								kind : kind
								,
							},
							method : 'post',
							callback : function(options, success, response) {
								main.getActiveTab().setLoading(false);
								var localJson = new Ext.decode(response.responseText);
								if (localJson.exceptionInfo) {
									showError(localJson.exceptionInfo);
									return "";
								}
								if (localJson.success) {
									Ext.Msg.alert("提示", "处理成功!", function() {
												me.BaseUtil.getActiveTab()
														.close();
												main.setActiveTab(panel);
											});
								}
							}
						});
					} else {
						showError('你未选择任何载入数据！');
						return
					}
				}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('team_id').value);
    			}
    		},
    		 'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMpsMain', '计划', 'jsps/plm/team/team.jsp');
    			}
    		},
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
	
});