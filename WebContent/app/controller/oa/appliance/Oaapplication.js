Ext.QuickTips.init();
Ext.define('erp.controller.oa.appliance.Oaapplication', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.appliance.Oaapplication','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','core.button.ResEnd','core.button.End',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Submit','core.form.FileField',
    		'core.button.ResAudit','core.button.ResSubmit','core.button.Audit','core.button.TurnOapurchase','core.button.TurnYPOut',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.form.MultiField',
    		'core.button.AllowNumber','core.button.Print','core.button.MRPResourceScan','core.button.TurnGoodsPicking'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(view,record){
    				this.onGridItemClick(view,record,me);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					console.log(344);
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				//数量不能为空或0
    				Ext.each(items, function(item){
						if(item.data['od_total'] == null||item.data['od_total']==0){
							item.set('od_total',item.data['od_number']);
						}
    				});
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('oa_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addOaapplication', '新增采购用品申请单', 'jsps/oa/appliance/oaapplication.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('oa_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('oa_id').value);
				}
			},
			'erpPrintButton': {
    			click:function(btn){
	    			var reportName='YP_App';
					var condition='{Oaapplication.oa_id}='+Ext.getCmp('oa_id').value+'';
					var id=Ext.getCmp('oa_id').value;
					me.FormUtil.onwindowsPrint(id,reportName,condition);
				}
    		},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('oa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('oa_id').value);
				}
			},
			'erpAllowNumberButton':{
				afterrender:function(btn){
					var status = Ext.getCmp('oa_statuscode');
					if(status && status.value == 'ENTERING'){
						btn.hide();
					}
    				btn.setDisabled(true);
    			},
				click: function(btn){
    				var id=btn.ownerCt.ownerCt.ownerCt.items.items[1].selModel.selected.items[0].data["od_id"];
    				var formCondition="od_id IS"+id;
    				var linkCaller='Oaapplication!allownum';    				
    				var win = new Ext.window.Window({  
						id : 'win',
						height : '90%',
						width : '95%',
						maximizable : true,
						buttonAlign : 'center',
						layout : 'anchor',
						items : [ {
							tag : 'iframe',
							frame : true,
							anchor : '100% 100%',
							layout : 'fit',
							 html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/oa/appliance/checkOaapplication.jsp?_noc=1&whoami='+linkCaller+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
						} ],
                        listeners:{
                          'beforeclose':function(view ,opt){
                        	   //grid  刷新一次
                        	  var grid=Ext.getCmp('grid');
                        	  var gridParam = {caller: caller, condition: gridCondition};
                        	  grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
                        	  Ext.getCmp('erpAllowNumberButton').setDisabled(true);
                          }	
                        }
					});
 					win.show(); 
    			}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('oa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('oa_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('oa_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('oa_id').value);
				}
			},
			'erpResEndButton':{
                afterrender: function(btn) {
                    var status = Ext.getCmp('oa_statuscode');
                    if (status && status.value != 'FINISH') {
                        btn.hide();
                    }
                },
                click: function(btn) {
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/appliance/resEndOaapplication.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id:Ext.getCmp('oa_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){    	    		    				
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});   					
                } 			              
			},
			'erpEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('oa_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	warnMsg("确定结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/appliance/endOaapplication.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id:Ext.getCmp('oa_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
                }
            },
			'erpTurnYPOutButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('oa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(){
					warnMsg("确定要转用品领用吗?", function(btn){
    					if(btn == 'yes'){
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var grid = Ext.getCmp('grid');
    						var jsonGridData = new Array();
							var s = grid.getStore().data.items;//获取store里面的数据
							for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
								var data = s[i].data;
								jsonGridData.push(Ext.JSON.encode(data));
							}
    						//var griddata = this.getGriddata(grid);
    						//console.log(griddata);
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/appliance/turnYPOut.action',
    	    			   		params: {
    	    			   			formdata : Ext.JSON.encode(r).toString(),
    	    			   			griddata : "["+jsonGridData.toString()+"]"
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				Ext.Msg.alert('提示','转用品领用成功！');
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
				}
			},
			'erpTurnGoodsPickingButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('oa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
				},
				click:function(){
	        		me.turn('Oaapplication!ToGoodPicking', 'od_oaid=' + Ext.getCmp('oa_id').value +' and nvl(od_turnlyqty,0) < nvl(od_total,0)', 'oa/appliance/turnGoodPicking.action');
	        	}
			},
			'erpTurnOapurchaseButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('oa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				var isturn = Ext.getCmp('oa_isturn');
    				if(isturn && isturn.value != '0'){
    					btn.hide();
    				}
    				btn.hide();
    			},
				click: function(){
					warnMsg("确定要转入用品采购单吗?", function(btn){
    					if(btn == 'yes'){
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var grid = Ext.getCmp('grid');
    						var jsonGridData = new Array();
							var s = grid.getStore().data.items;//获取store里面的数据
							for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
								var data = s[i].data;
								jsonGridData.push(Ext.JSON.encode(data));
							}
    						//var griddata = this.getGriddata(grid);
    						//console.log(griddata);
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/appliance/turnOaPurchase.action',
    	    			   		params: {
    	    			   			formdata : Ext.JSON.encode(r).toString(),
    	    			   			griddata : "["+jsonGridData.toString()+"]"
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				Ext.Msg.alert('提示','转用品采购成功！');
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
				}
			}
    	});
    },
    onGridItemClick: function(selModel,record,me){//grid行选择
    	this.GridUtil.onGridItemClick(selModel,record);
    	var grid=selModel.ownerCt;
    	var show=0;
    	Ext.Array.each(grid.necessaryFields, function(field) {
    	var fieldValue=record.data[field];
           if(fieldValue==undefined||fieldValue==""||fieldValue==null){
        	   show=1;
        	   return; 
           }
        });
    	if(show==1){
    		if(Ext.getCmp('erpAllowNumberButton')!=null)
        	Ext.getCmp('erpAllowNumberButton').setDisabled(true);
    	}else {
    		if(Ext.getCmp('erpAllowNumberButton')!=null)
    		Ext.getCmp('erpAllowNumberButton').setDisabled(false);
		}
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	turn: function(nCaller, condition, url){
	    var win = new Ext.window.Window({
		    id : 'win',
			height: "100%",
			width: "80%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
				    + "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}],
			buttons : [{
				name: 'confirm',
				text : $I18N.common.button.erpConfirmButton,
				iconCls: 'x-button-icon-confirm',
				cls: 'x-btn-gray',
				    listeners: {
   				    	buffer: 500,
   				    	click: function(btn) {
   				    		var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
   	   				    	btn.setDisabled(true);
   	   				    	grid.updateAction(url);
   	   				    	window.location.reload();
   				    	}
   				    }
			}, {
				text : $I18N.common.button.erpCloseButton,
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(){
					Ext.getCmp('win').close();
				}
			}]
		});
		win.show();
    }
});