Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.BillARSplit', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gs.BillARSplit','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Close','core.button.Split',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: function(selModel, record) {
					this.onGridItemClick(selModel, record);   
					if (record.data.brs_id != 0 && record.data.brs_id != null && record.data.brs_id != '') {
                        var btn = Ext.getCmp('cancelSplitDetail');
                        if(record.data.brs_nowstatus == '已拆分'){
                        	btn && btn.setDisabled(false);
                        } else {
                        	btn && btn.setDisabled(true);
                        }
                        btn = Ext.getCmp('splitDetail');
                        if(record.data.brs_nowstatus == '未拆分'){
                        	btn && btn.setDisabled(false);
                        } else {
                        	btn && btn.setDisabled(true);
                        }
                        btn = Ext.getCmp('toolbar').down('erpDeleteDetailButton');
                        if(record.data.brs_nowstatus == '已拆分'){
               		 		btn && btn.setDisabled(true);
                        } else {
                        	btn && btn.setDisabled(false);
                        }
               		 	
                    }      
				},
				afterrender: function(g) {
					g.plugins[0].on('beforeedit', function(args) {
						var status = args.record.data.brs_nowstatus;
						if(status == '已拆分') {
    						return false;
    					}
					});
				}
			},
			'erpSaveButton': {
 			   click: function(btn){
 				  var grid = Ext.getCmp('grid'), items = grid.store.data.items;
 		    	  Ext.each(items, function(item) {
	                  if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
	 		    		  if(item.data['brs_duedate'] != null && item.data['brs_duedate'] != "" && item.data['brs_date'] != null && item.data['brs_date'] != ""){
	 		    			 if (Ext.Date.format(item.data['brs_duedate'],'Y-m-d') < Ext.Date.format(item.data['brs_date'],'Y-m-d')) {
				                   showError('到期日期小于票据日期，不能拆分！');
				                   return;
				              }
	 		    		  }
	 		    		  if(item.data['brs_duedate'] != null && item.data['brs_duedate'] != "" && item.data['brs_outdate'] != null && item.data['brs_outdate'] != ""){
	 		    			 if (Ext.Date.format(item.data['brs_duedate'],'Y-m-d') < Ext.Date.format(item.data['brs_outdate'],'Y-m-d')) {
				                   showError('到期日期小于出票日期，不能拆分！');
				                   return;
				              }
	 		    		  }
	                  }
 		    	  });
 		    	 this.FormUtil.onUpdate(this);
 			   }
			},
 		    'erpSplitButton': {
               afterrender: function(btn) {
                   var status = Ext.getCmp('bar_statuscode');
                   if (status && status.value != 'AUDITED') {
                       btn.hide();
                   }
               },
               click: function(btn) {
            	   warnMsg("确定将明细未拆分的全部进行拆分吗?", function(btn){
            		   if(btn == 'yes'){
            			    var grid = Ext.getCmp('grid');
   							me.FormUtil.getActiveTab().setLoading(true);//loading...
   							Ext.Ajax.request({
   								url : basePath + 'fa/gs/splitBillAR.action',
   								params: {
   									id: Ext.getCmp('bar_id').value
   								},
   								method : 'post',
   								callback: function(opt, s, res) {
   									me.FormUtil.getActiveTab().setLoading(false);
		   	    			   		var r = new Ext.decode(res.responseText);
		   	    			   		if (r.success) {
		   	    			   			grid.GridUtil.loadNewStore(grid, {
		   	    			   				caller: caller,
		   	    			   				condition: gridCondition
		   	    			   			});
		   	    			   			showMessage('提示', '拆分成功!', 1000);
		   	    			   		} else if (r.exceptionInfo) {
		   	    			   			showError(r.exceptionInfo);
		   	    			   		} else {
		   	    			   			saveFailure();
		   	    			   		}
   								}
   							});
            		   }
            	   });
               }
           },
           /**
		    * 明细行拆分
		    */
           '#splitDetail': {
        	   	click: function(btn) {
		            var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
		            me.splitDetail(record);
		        }
           },
           /**
		    * 明细行取消拆分
		    */
           '#cancelSplitDetail': {
        	   	click: function(btn) {
		            var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
		            me.cancelSplitDetail(record);
		        }
           },
           'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
           }
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	splitDetail: function(record){
    	var brs_id = record.data.brs_id, grid = Ext.getCmp('grid');
        if (record.data.brs_nowstatus != '未拆分' ) {
            showError('只能对未拆分的明细进行拆分！');
            return;
        } else {
            Ext.Ajax.request({
                url: basePath + 'fa/gs/splitDetailBillAR.action',
                params:{
    				id: brs_id
    			},
                method: 'post',
                callback: function(opt, s, res) {
                    var r = new Ext.decode(res.responseText);
                    if (r.success) {
                        grid.GridUtil.loadNewStore(grid, {
                            caller: caller,
                            condition: gridCondition
                        });
                        showMessage('提示', '拆分成功!', 1000);
                    } else if (r.exceptionInfo) {
                        showError(r.exceptionInfo);
                    } else {
                        saveFailure();
                    }
                }
            });
        }
    },
    cancelSplitDetail: function(record){
    	var brs_id = record.data.brs_id, grid = Ext.getCmp('grid');
        if (record.data.brs_nowstatus != '已拆分' ) {
            showError('只能对已拆分的明细进行拆分！');
            return;
        } else {
            Ext.Ajax.request({
                url: basePath + 'fa/gs/cancelSplitDetailBillAR.action',
                params:{
    				id: brs_id
    			},
                method: 'post',
                callback: function(opt, s, res) {
                    var r = new Ext.decode(res.responseText);
                    if (r.success) {
                        grid.GridUtil.loadNewStore(grid, {
                            caller: caller,
                            condition: gridCondition
                        });
                        showMessage('提示', '取消拆分成功!', 1000);
                    } else if (r.exceptionInfo) {
                        showError(r.exceptionInfo);
                    } else {
                        saveFailure();
                    }
                }
            });
        }
    }
});