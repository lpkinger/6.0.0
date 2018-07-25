Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.Feeder', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.MakePrepare','core.form.Panel','core.grid.Panel2','core.button.Delete',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.ResAudit','core.button.Update','core.button.TurnProdIOGet','core.button.ResSubmit',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn','core.button.Submit','common.datalist.Toolbar',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Audit','common.datalist.GridPanel',
    		'core.button.Scrap','core.button.RepairLog'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': {
    			reconfigure: function(grid){
    				Ext.defer(function(){
    					grid.readOnly = true;
    				}, 500);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
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
    				me.FormUtil.onDelete(Ext.getCmp('fe_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addFeeder', '新增飞达', 'jsps/pm/mes/feeder.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fe_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('fe_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fe_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('fe_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fe_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('fe_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fe_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('fe_id').value);
    			}
    		},
    		'#fe_id':{
    			afterrender:function(a){
    				if(!a || !a.value){   					
    				   Ext.getCmp('tab').setVisible(false);
    				}
    			}
    		},
    		'tabpanel > #tab-list': {
				activate: function(panel) {
					var fecode = Ext.getCmp('fe_code').value, condition;
					if(!Ext.isEmpty(fecode) ){
        				condition = "msl_fecode='" + fecode + "'";
        	    	}
					if(panel.boxReady) {
						var grid = Ext.getCmp('grid2');
	        			if(grid) {
	            			grid.formCondition = condition;
	            			grid.getCount(null, grid.getCondition() || '');
	        			}
					} else {
						panel.boxReady = true;			
						panel.add({
							xtype: 'erpDatalistGridPanel',
							caller: 'FeederUseRecord',
							anchor: '100% 100%',
							id:'grid2',
							formCondition: condition
						});
					}
				}
			},
			'erpRepairLogButton':{//维修登记
				click:function(btn){
					var win = new Ext.window.Window({  
			    		  modal : true,
			        	  id : 'win',
			        	  height : '35%',
			        	  width : '30%',       	 
			        	  layout : 'anchor',   
			        	  bodyStyle: 'background: #f1f1f1;',
						  bodyPadding:5,			  
			        	  items : [{
			        	  	anchor: '100% 100%',
			                xtype: 'form',
			                bodyStyle: 'background: #f1f1f1;',
				            items:[{
				        		  xtype:'textareatrigger',
				        		  name:'bad_remark',
				        		  fieldLabel:'故障描述',
				        		  id:'bad_remark',
				        		  allowBlank:false   ,
				        		  fieldStyle : "background:rgb(224, 224, 255);",    
							      labelStyle:"color:red;"
				        	  },{
				        	  	 xtype: 'checkbox',
			                     boxLabel  : '累计数清零',
			                     name      : 'ifclear',
			                     checked   : false,
			                     id        : 'ifclear',
				        		 allowBlank:false
				        	  }],
			                buttonAlign : 'center',
				            buttons: [{
								text: '确定'	,
								cls: 'x-btn-gray',
								iconCls: 'x-button-icon-save',
								id:'confirmBtn',
								formBind: true, //only enabled once the form is valid
			                    handler: function(btn) {      			                    	
			    					me.saveRepairLog(Ext.getCmp('bad_remark').value,Ext.getCmp('ifclear').checked);   			 		    					
								  }
							  },{
							    text: '取消'	,
								cls: 'x-btn-gray',
								iconCls: 'x-button-icon-close',
			                    handler: function(btn) {                   	                  				    			 
			    					win.close();
								}
							  }]
			    	       }]
			    		});
    	           win.show(); 
				},
				afterrender:function(btn){
					var status = Ext.getCmp('fe_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
				}
			},
			'erpScrapButton':{//报废
				click:function(btn){
					var win = new Ext.window.Window({  
			    		  modal : true,
			        	  id : 'win',
			        	  height : '35%',
			        	  width : '30%',       	 
			        	  layout : 'anchor',   
			        	  bodyStyle: 'background: #f1f1f1;',
						  bodyPadding:5,			  
			        	  items : [{
			        	  	anchor: '100% 100%',
			                xtype: 'form',
			                bodyStyle: 'background: #f1f1f1;',
			                defaults:{
			        	  	  fieldStyle : "background:rgb(224, 224, 255);",    
							  labelStyle:"color:red;"
			        	    },
				            items:[{
				        		  xtype:'textareatrigger',
				        		  name:'scrap_remark',
				        		  fieldLabel:'报废原因',
				        		  id:'scrap_remark',
				        		  allowBlank:false       		 
				        	  }],
			                buttonAlign : 'center',
				            buttons: [{
								text: '确定'	,
								cls: 'x-btn-gray',
								iconCls: 'x-button-icon-save',
								id:'confirmBtn',
								formBind: true, //only enabled once the form is valid
			                    handler: function(btn) { 			                    
			    					me.saveScrapLog(Ext.getCmp('scrap_remark').value);		    				
								  }
							  },{
							    text: '取消'	,
								cls: 'x-btn-gray',
								iconCls: 'x-button-icon-close',
			                    handler: function(btn) {                   	                  				    			 
			    					win.close();
								}
							  }]
			    	       }]
			    		});
    	           win.show(); 
				},
				afterrender:function(btn){
					var status = Ext.getCmp('fe_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
				}
			}
			
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	saveRepairLog:function(a,b){//保存维修登记
	    Ext.getCmp('win').close();
		var fe_id = Ext.getCmp("fe_id").value;
		Ext.Ajax.request({
			url: basePath + 'pm/mes/saveFeederRepairLog.action',
			params: {
				caller : caller,
				fe_id  : fe_id,
				remark : a,
				ifclear: b
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}else{
					showMessage('提示','保存维修记录成功'); 		
					var grid = Ext.getCmp('grid1');
					var gridParam = {caller: caller, condition: 'fe_id='+fe_id, _m: 0}
					grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
				}
			}
		});
	},
	saveScrapLog:function(data){//报废记录
		var remark = data,fe_id=Ext.getCmp('fe_id').value;
		Ext.getCmp('win').close();
		Ext.Ajax.request({
			url: basePath + 'pm/mes/saveFeederScrapLog.action',
			params: {
				caller: caller,
				fe_id : fe_id,
				remark: remark
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}else{
					showMessage('提示','报废成功'); 		
					var grid = Ext.getCmp('grid1');
					var gridParam = {caller: caller, condition: 'fe_id='+fe_id, _m: 0}
					grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
				}
			}
		});
	}
});