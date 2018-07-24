Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.Feature', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.Feature','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.UpdateFeature',
      		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
  			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
  			'core.grid.YnColumn','core.grid.YnColumnNV'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
			'erpUpdateFeatureButton': {
				afterrender: function(btn){
					var val = Ext.getCmp('fe_status').value;
					if(val != '已审核'){
						btn.hide();
					} else {
						Ext.getCmp('form').fename = Ext.getCmp('fe_name').value;//初始化fename
						btn.show();
					}
				},
				click: function(btn){
					if(Ext.getCmp('fe_name').readOnly == true){
						Ext.getCmp('fe_name').setReadOnly(false);alert('可以修改特征名称了,修改完再次点击当前按钮即可保持修改');return;					
					}
					if(Ext.getCmp('fe_name').value != Ext.getCmp('form').fename){
						me.updateFeature();
					} else {
						showError('特征名称未做修改哦！');return;
					}
				}
			},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('Feature',2,'fe_code');//自动添加编号
    				}
    				var flag = this.check();
    				if(flag != 0){//更新前 业务逻辑检验
    					return;
    				}
    				//保存之前的一些前台的逻辑判定
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
    				var flag = this.check();
    				if(flag != 0){//更新前 业务逻辑检验
    					return;
    				}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				if(Ext.getCmp('fe_statuscode').value != 'ENTERING'){
    					btn.hide();
    				} else {
    					btn.show();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('fe_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addFeature', '新增特征', 'jsps/pm/bom/Feature.jsp');
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
    		'textfield[id=fe_name]': {
    			blur: function(field){
    				if(field.value != null && Ext.getCmp('fe_status').value != '已审核'){
    					me.checkName();
    				}
    			}
    		},
    		'textfield[id=fe_status]': {//坑爹 用statuscode就是不行
    			afterrender: function(field){
    				if(field.value != '已审核'){
    					Ext.getCmp('addDetail').setDisabled(true);
    					Ext.getCmp('updateRemark').setDisabled(true);
    					Ext.getCmp('forbidden').setDisabled(true);
    					Ext.getCmp('noforbidden').setDisabled(true);
    					Ext.getCmp('toyf').setDisabled(true);
    					Ext.getCmp('tobz').setDisabled(true);
    				} else {
    					Ext.getCmp('addDetail').setDisabled(false);
    					Ext.getCmp('updateRemark').setDisabled(false);
    					Ext.getCmp('forbidden').setDisabled(false);
    					Ext.getCmp('noforbidden').setDisabled(false);
    					Ext.getCmp('toyf').setDisabled(false);
    					Ext.getCmp('tobz').setDisabled(false);
    				}
    			}
    		},
    		'button[id=addDetail]': {
    			click: function(btn){
    				me.addDetail();
    			}
    		},
    		'combocolumn[dataIndex=fr_value]':{
    			beforerender:function(column){    				
    				column.editor= {
						format:'',
						xtype: 'combo',
						listConfig:{
							maxHeight:180
						},
						store: {
							fields: ['display', 'value'],
							data :[],
						},
						displayField: 'display',
						editable:false,
						valueField: 'value',
						queryMode: 'local',
						onTriggerClick:function(trigger){
							if(findData.length<1){
							Ext.Array.each(Ext.getCmp('grid').getStore().data.items,function(item){
								findData.push({
									display:item.data.fd_value,
									value:item.data.fd_value,
									code:item.data.fd_valuecode
								});
							});
							}
							var me=this;
						    this.getStore().loadData(findData);
							if (!me.readOnly && !me.disabled) {
								if (me.isExpanded) {
									me.collapse();
								} else {
									me.expand();
								}
								me.inputEl.focus();
							}    
						},
						listeners:{
							select:function(field,records){
								column.ownerCt.ownerCt.getSelectionModel().selected.items[0].set('fr_valuecode',records[0].data.code);
							}
						}
					};
    			}
    		 },
    		'button[id=forbidden]': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.fd_statuscode != 'DISABLED'){
    					Ext.Ajax.request({
    				   		url : basePath + 'pm/bom/updateByCondition.action',
    				   		params : {
    				   			tablename:'FeatureDetail',
    				   			condition: 'fd_id=' + record.data.fd_id,
    				   			update: "fd_status='已禁用',fd_statuscode='DISABLE',fd_log='"+em_name + Ext.util.Format.date(new Date(), 'Y-m-d H:i:s') + "禁用'"
    				   		},
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			me.GridUtil.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    				   			if(localJson.exceptionInfo){
    			        			showError(localJson.exceptionInfo);return;
    			        		}
    				   			if(localJson.success){
    				   				window.location.reload();
    				   			}
    				   		}
        				});
    				} else {
    					showError('该明细已经被禁用');
    				}
    			}
    		},
    		'button[id=noforbidden]': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.fd_statuscode == 'DISABLE'){
    					Ext.Ajax.request({
    						url : basePath + 'pm/bom/updateByCondition.action',
    				   		params : {
    				   			tablename:'FeatureDetail',
    				   			condition: 'fd_id=' + record.data.fd_id,
    				   			update: "fd_status='可用',fd_statuscode='ABLED',fd_log='"+em_name + Ext.util.Format.date(new Date(), 'Y-m-d H:i:s') + "反禁用'"
    				   		},
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			me.GridUtil.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    				   			if(localJson.exceptionInfo){
    			        			showError(localJson.exceptionInfo);return;
    			        		}
    				   			if(localJson.success){
    				   				window.location.reload();
    				   			}
    				   		}
        				});
    				} else {
    					showError('该明细已经是可用');
    				}
    			}
    		},
    		'button[id=toyf]': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.fd_style != '非标准'){
    					Ext.Ajax.request({
    				   		url : basePath + 'pm/bom/updateByCondition.action',
    				   		params : {
    				   			tablename:'FeatureDetail',
    				   			condition: 'fd_id=' + record.data.fd_id,
    				   			update: "fd_style='非标准',fd_log='"+em_name + Ext.util.Format.date(new Date(), 'Y-m-d H:i:s') + "转非标准'"
    				   		},
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			me.GridUtil.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    				   			if(localJson.exceptionInfo){
    			        			showError(localJson.exceptionInfo);return;
    			        		}
    				   			if(localJson.success){
    				   				window.location.reload();
    				   			}
    				   		}
        				});
    				} else {
    					showError('该明细BOM阶段已经是非标准');
    				}
    			}
    		},
    		'button[id=tobz]': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.fd_style != '标准'){
    					Ext.Ajax.request({
    				   		url : basePath + 'pm/bom/updateByCondition.action',
    				   		params : {
    				   			tablename:'FeatureDetail',
    				   			condition: 'fd_id=' + record.data.fd_id,
    				   			update: "fd_style='标准',fd_log='"+em_name + Ext.util.Format.date(new Date(), 'Y-m-d H:i:s') + "转标准'"
    				   		},
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			me.GridUtil.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    				   			if(localJson.exceptionInfo){
    			        			showError(localJson.exceptionInfo);return;
    			        		}
    				   			if(localJson.success){
    				   				window.location.reload();
    				   			}
    				   		}
        				});
    				} else {
    					showError('该明细BOM阶段已经是标准');
    				}
    			}
    		},
    		'button[id=updateRemark]': {
    			click: function(btn){
    				me.updateRemark();
    			}
    		},
    		'gridcolumn[name=fr_value]': {
    			afterrender: function(field){
    				console.log(field);
    			}
    		},
    		'field[name=fd_ifdefault]': {
    			change: function(field){
    				if(field.value == -1){
    					var grid = Ext.getCmp('grid');
    					var items = grid.store.data.items;
    					Ext.each(items, function(item){
    						if(item.data.fd_ifdefault==-1 && item.data.fd_detno != grid.selModel.lastSelected.data.fd_detno){
    							console.log(item.data.fd_detno+ " == "+item.data.fd_ifdefault);
    							item.data.fd_ifdefault = 0;
    						}
    					});
    				}
    			}
    		},
		});
	}, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	check: function(){
		var flag=0;
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		Ext.each(items, function(item, i){
			if(item.dirty && flag==0 && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data.fd_value == '' || item.data.fd_value == null){
					showError("特征值不能为空");flag=1;//return;
				}else if(item.data.fd_valuecode == '' || item.data.fd_valuecode == null){
					showError("特征值码不能为空");flag=1;//return;
				}else if(item.data.fd_style == ''||item.data.fd_style == null){
					showError("BOM阶段不能为空"); flag=1;
				}
				Ext.each(items, function(t, j){
					if(i != j && item.data.fd_value == t.data.fd_value){
						showError("特征值不能重复");flag=1;//return;
					}else if(i != j && item.data.fd_valuecode == t.data.fd_valuecode){
						showError("特征值码不能重复");flag=1;//return;
					}
				});
				
			}
		});
		return flag;
	},
	checkName: function(){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/checkName.action",
        	params: {
        		name: Ext.getCmp('fe_name').value
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success){
        			if(res.flag == 0){
//        				window.location.href = window.location.href;
        			} else if (res.flag == 1){
        				showError('特征名称已经存在,请重新修改');return;
        			}
        		}
        	}
		});
	},
	updateFeature: function(){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/updateFeatureName.action",
        	params: {
        		id: Ext.getCmp('fe_id').value,
        		name: Ext.getCmp('fe_name').value
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success){
        			if(res.flag == 0){
        				alert('更新成功');
        				window.location.href = window.location.href;
        			} else if (res.flag == 1){
        				showError('特征名称已经存在,请重新修改');return;
        			}
        		}
        	}
		});
	},
	addDetail: function(){
		var me = this;
		var win = new Ext.window.Window({
			id : 'win',
			title: "添加特征明细",
			height: "50%",
			width: "60%",
			maximizable : false,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				xtype: 'erpGridPanel2',
				anchor: '100% 100%', 
				id:'grid3',
				condition: '1<>1',
				readOnly: false,
				caller:'FeatureDetail',
				detno: 'fd_detno',
				keyField: 'fd_id',
				mainField: 'fd_feid',
				necessaryField: 'fd_value',
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				     clicksToEdit: 1
				})],
			}],
			bbar: ['->',{
				text:'编辑',
				handler: function(btn){
					var grid=Ext.getCmp('grid3');
					if(grid.readOnly){
						grid.readOnly=false;
						btn.hide();
					} else {
						btn.hide();
					}
					
				}
				},{
				text:'保存',
				listeners: {
					click: function(){
					var grid = Ext.getCmp('grid3');
					var detno = Ext.getCmp('grid').getStore().getCount();
					console.log(grid.store.data.items);
					var error = '';
					Ext.each(grid.store.data.items, function(item){
						if(item.dirty){
							if(item.data.fd_value == '' || item.data.fd_value == null){
								error = "特征值不能为空";return;
							}
							if(item.data.fd_valuecode == '' || item.data.fd_valuecode == null){
								error = "特征值码不能为空";return;
							}
							Ext.each(Ext.getCmp('grid').store.data.items, function(it, i){
								if(item.data.fd_value == it.data.fd_value || item.data.fd_valuecode == it.data.fd_valuecode){
									error = "特征值或特征值码已经存在，不能重复";return;
								}
							});							
						}
					});
					if(error != ''){
						showError(error);return;
					}
					Ext.each(grid.store.data.items, function(item){
						if(item.dirty == true){
							item.set('fd_code', Ext.getCmp('fe_code').value);
							item.set('fd_detno', ++detno);
							item.set('fd_log', em_name + '(' + em_code + ') 于 ' + Ext.util.Format.date(new Date(), 'Y-m-d H:i:s') + " 增加特征明细");
						}
					});
					var param = me.GridUtil.getGridStore(grid);
					if(grid.necessaryField.length > 0 && (param == null || param == '')){
						showError('明细表还未添加数据');return;
					} else {
						me.GridUtil.getActiveTab().setLoading(true);//loading...
						Ext.Ajax.request({
					   		url : basePath + 'pm/bom/addFeatureDetail.action',
					   		params : {
					   			param: unescape(param.toString().replace(/\\/g,"%"))
					   		},
					   		method : 'post',
					   		callback : function(options,success,response){
					   			me.GridUtil.getActiveTab().setLoading(false);
					   			var localJson = new Ext.decode(response.responseText);
					   			if(localJson.exceptionInfo){
				        			showError(localJson.exceptionInfo);return;
				        		}
					   			if(localJson.success){
//				    				window.close();
				    				window.location.href = window.location.href;
				    			}
				    		}
						});
					}
				}
				}
			},'->']
		});
		win.show();
	},
	updateRemark: function(selModel, record){
		var me = this;
		var grid = Ext.getCmp('grid');
		var record = grid.selModel.lastSelected;
		var win = new Ext.window.Window({
			id : 'win2',
			title: "修改明细备注 (" + record.data.fd_id + ")",
			height: "200px",
			width: "400px",
			maximizable : false,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				xtype: 'textarea',
				anchor : '100% 100%',
				value: record.data.fd_remark,
				id:'remark',
			}],
			bbar: ['->',{
				iconCls: 'x-button-icon-submit',
		    	cls: 'x-btn-gray',
		    	text: '修  改',
				handler: function(){
					Ext.Ajax.request({
				   		url : basePath + 'pm/bom/updateRemark.action',
				   		params : {
				   			id: record.data.fd_id,
				   			remark: Ext.getCmp('remark').value
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			me.GridUtil.getActiveTab().setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
			        			showError(localJson.exceptionInfo);return;
			        		}
				   			if(localJson.success){
//			    				window.close();
			    				window.location.href = window.location.href;
			    			}
			    		}
					});
				}
			},'->']
		});
		win.show();
	}
});