Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.SaleForecast', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','drp.distribution.SaleForecast','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ChangeDate',
      		'core.button.ResAudit','core.button.Scan','core.button.DeleteDetail','core.button.ResSubmit','core.button.FeatureDefinition',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.End','core.button.ResEnd','core.button.Print',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.FeatureView'
  	],
	init:function(){
		var me = this;
		this.control({
		   'erpGridPanel2': { 
				itemclick: function(selModel, record){
					this.onGridItemClick
				}
			},
		   'erpChangeDateButton':{
		     afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
			  click:function(btn){
			  var keyvalue=Ext.getCmp('sf_id').value;
			  var condition='sd_sfid='+keyvalue;
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
   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=SaleForecast!Change' 
   				    	  +"&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
   				    }],
   				    buttons : [{
   				    	text : $I18N.common.button.erpConfirmButton,
   				    	iconCls: 'x-button-icon-confirm',
   				    	cls: 'x-btn-gray',
   				    	handler : function(){
   				    		var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
   				    		var data = grid.getEffectData();                      
		                if(data != null){
			               grid.setLoading(true);
			               Ext.Ajax.request({
		   		           url : basePath + 'scm/sale/SaleForecastChangedate.action',
		   		           params: {
		   			            caller: caller,
		   			            data: Ext.encode(data)
		   		            },
		   		           method : 'post',
		   		          callback : function(options,success,response){
		   			           grid.setLoading(false);
		   			           var localJson = new Ext.decode(response.responseText);
		   			           if(localJson.exceptionInfo){
		   				       showError(localJson.exceptionInfo);
		   				      return "";
		   			        }
	    			        if(localJson.success){
	    				         if(localJson.log){
	    					    showMessage("提示", localJson.log);
	    				     }
		   				     Ext.Msg.alert("提示", "处理成功!", function(){
		   					     win.close();
		   					   var detailgrid= Ext.getCmp('grid');		   					   
		   					    gridParam = {caller: 'SaleForecast', condition: condition};
		   					   me.GridUtil.getGridColumnsAndStore(detailgrid, 'common/singleGridPanel.action', gridParam, "")
		   				});
		   			}
		   		}
			});
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
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.beforeSaveSaleForecast(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('sf_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.beforeUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSaleForecast', '新增销售预测单', 'jsps/drp/distribution/saleForecast.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('sf_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sf_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('sf_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sf_id').value);
				}
			},
			'erpPrintButton': {
				click:function(btn){
				var reportName="SaleForecastAudit1";
				var condition='{SaleForeCast.sf_id}='+Ext.getCmp('sf_id').value+'';
				var id=Ext.getCmp('sf_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
			},
			'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sf_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onEnd(Ext.getCmp('sf_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sf_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('sf_id').value);
    			}
    		},
			'textfield[name=sf_fromdate]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var date = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('sd_startdate',date);
						});
					}
				}
    		},
			'textfield[name=sf_todate]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var date = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('sd_enddate',date);
						});
					}
				}
    		},
			'textfield[name=sf_custcode]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var date = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('sd_custcode',date);
						});
					}
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
	beforeSaveSaleForecast: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('sf_code').value;
	    Ext.Array.each(items, function(item){
	    	item.set('sd_code', c);
		});
		//保存
	    this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('sf_code').value;
	    Ext.Array.each(items, function(item){
	    	item.set('sd_code', c);
		});
		//更新
	    this.FormUtil.onUpdate(this);	
	}
});