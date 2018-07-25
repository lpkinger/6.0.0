Ext.QuickTips.init();
Ext.define('erp.controller.oa.custom.MultiForm', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil'],
    views:[
   		'oa.custom.MultiForm','oa.custom.CustomGrid','ma.MyForm','ma.MyDetail','core.button.DeleteDetail','core.toolbar.Toolbar',
   		'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.grid.TfColumn','core.grid.YnColumn',
   		'core.button.UUListener', 'core.button.DbfindButton','core.button.ComboButton', 'core.form.YnField',
   		'core.button.Sync','core.form.CheckBoxGroup'
   	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	this.control({ 
    		'erpSyncButton': {
    			afterrender: function(btn){
    				btn.autoClearCache = true;
    			}
    		},
    		'multidbfindtrigger': {
    			render: function(field){
    				if(field.name == 'fo_button4add' || field.name == 'fo_button4rw'){
    					var fields = Ext.Object.getKeys($I18N.common.button);
    					var values = Ext.Object.getValues($I18N.common.button);
    					var data = [];
    					Ext.each(fields, function(f, index){
    						var o = {};
    						o.value = fields[index];
    						o.display = values[index];
    						data.push(o);
    					});
    					field.multistore = {fields:['display', 'value'],data:data};
    				}
    			}
    		},
    		'mygrid': {
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    				var grid=Ext.getCmp('grid');
    				if(record&&record.data.fd_dbfind != 'F') {
    					grid.down('erpDbfindButton').setDisabled(false);  
    					grid.down('erpComboButton').setDisabled(true);   	
    				}else if(record && record.data.fd_type == 'C') {
    					grid.down('erpComboButton').setDisabled(false);
    					grid.down('erpDbfindButton').setDisabled(true);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    					grid.down('erpDbfindButton').setDisabled(true);
    				}
    			}
    		},
    		'mydetail': {
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'detail');
    				var grid=Ext.getCmp('detail');
    				if(record&&record.data.dg_dbbutton != '0') {
    					grid.down('erpDbfindButton').setDisabled(false); 
    					grid.down('erpComboButton').setDisabled(true);
    				}else if(record && (record.data.dg_type == 'combo' || record.data.dg_type=='editcombo')) {
    					grid.down('erpDbfindButton').setDisabled(true); 
    					grid.down('erpComboButton').setDisabled(false);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    					grid.down('erpDbfindButton').setDisabled(true);
    				}
    			}
    		},
    		'button[name=save]': {
    			click: function(btn){
    				me.save();
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    				  xtype:'erpDbfindButton'
    				});
    				btn.ownerCt.add({
    					xtype:'erpComboButton'
    				});
    			}
    		},
    		/**
    		 * 下拉框设置
    		 */
    		'erpComboButton': {
    			click: function(btn){
    				var activeTab = btn.up('tabpanel').getActiveTab();
    				var record = activeTab.down('gridpanel').selModel.lastSelected;
    				if(record) {
    					if(activeTab.id == 'maintab'){
    	    				if(record.data.fd_type == 'C') {   					
    	    					btn.comboSet(Ext.getCmp('fo_caller').value, record.data.fd_field);
    	    				}
        				} else {
        					if(record.data.dg_type == 'combo') 
        						btn.comboSet(Ext.getCmp('fo_caller').value, record.data.dg_field);
        				}
    				}
    			}
    		},
    		/**
    		 * DBFind设置
    		 */
    		'erpDbfindButton': {
    			click: function(btn){
    				var activeTab = btn.up('tabpanel').getActiveTab();
    				var record = activeTab.down('gridpanel').selModel.lastSelected;
    				if(record) {
    					if(activeTab.id == 'maintab'){
        					if(record.data.fd_dbfind != 'F') 
        						btn.dbfindSetUI(Ext.getCmp('fo_caller').value, record.data.fd_field);
        				}else {	
        					if(record.data.dg_dbbutton != '0') 
        						btn.dbfindSetGrid(Ext.getCmp('fo_caller').value, activeTab.down('gridpanel'), record.data.dg_field);
        				}
    				}
    			}
    		},
    		'button[name=delete]': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('fo_id').value);
    			}
    		},
    		'button[name=close]': {
    			beforerender:function(btn){
    				btn.handler=function(){
    					parent.Ext.getCmp('mainwin').close();
    				};
    			}
    		},
    		/*'textfield[name=fo_table]': {
    			change: function(field){
    				var grid = Ext.getCmp('grid');
    				if(grid) {
    					var val = field.value.toUpperCase().split(' ')[0];
    					Ext.Array.each(grid.store.data.items, function(item){
        					if(item.data['fd_field'] != null && item.data['fd_field'] != ''){
        						var t = item.data['fd_table'];
        						if(val != t.toUpperCase()){
        							item.set('fd_table', val);
        						}
        					}
        				});
    				}
    			}
    		},*/
    		'textfield[name=fo_detailtable]': {
    			change: function(field){
    				var grid = Ext.getCmp('detail');
    				if(grid) {
    					var val = field.value.toUpperCase().split(' ')[0];
        				Ext.each(grid.store.items, function(){
        					var t = this.data['dg_table'];
        					if(t != null && t != ''){
        						if(val != t.toUpperCase()){
        							this.set('dg_table', val);
        						}
        					}
        				});
    				}
    			}
    		},
    		'panel[id=detailtab]': {
    			activate: function(){
//    				var dt = Ext.getCmp('fo_detailtable').value;
//					if(dt == null || dt == ''){
//						showError("[主表]->[从表资料]->[明细表名]还未填写!");
//						Ext.getCmp('mytab').setActiveTab(0);
//					} else {
//						//判断detailtable的主键字段是否加到了detailgrid里面
//						me.insertKeyField();
//						//判断detailtable与主表关联的字段是否加到了detailgrid里面
//						me.insertMainField();
//					}
    			}
    		},
    		'button[name=preview]': {
    			click: function(){
    				
    			}
    		},
    		'erpCloseButton':{
    			beforerender:function(btn){
    				btn.handler= function(btn){
    						var win = parent.Ext.ComponentQuery.query('window');
    						if(win){
    							Ext.each(win, function(){
    								this.close();
    							});
    						} else {
    							window.close();
    						}
    			};
    			}
    		}
    	});
    },
    insertKeyField: function(){
    	var grid = Ext.getCmp('detail');
    	var field = Ext.getCmp('fo_detailkeyfield');
		var count = 0;
		Ext.each(grid.store.data.items, function(){
			var logic = this.data['dg_logictype'];
			var t = this.data['dg_field'];
			if(field.value.toUpperCase() == t.toUpperCase()){
				this.set('dg_logictype', 'keyField');
				logic = 'keyField';
				if(count >= 2){
					if(this.data['dg_id'] == null || this.data['dg_id'] == ''){
						grid.store.remove(this);
					}
				}
			}
			if(logic == 'keyField'){
				count++;
				if(count < 2 && field.value.toUpperCase() != t.toUpperCase()){
					this.set('dg_field', field.value);
				}
				if(count >= 2){
					if(this.data['dg_id'] == null || this.data['dg_id'] == ''){
						grid.store.remove(this);
					}
				}
			}
		});
		if(count == 0){
			grid.store.add({
				dg_sequence: grid.store.data.items[grid.store.data.length-1].data['dg_sequence'] + 1,
				dg_logictype: 'keyField',
				dg_field: field.value,
				dg_caption: 'ID',
				dg_table: Ext.getCmp('fo_detailtable').value,
				dg_caller: Ext.getCmp('fo_caller').value,
				dg_width: 0,
				dg_visible: '0',
				dg_type: 'numbercolumn',
				dg_editable: '0',
				dg_dbbutton: '0'
			});
		} else if(count > 1){
			showError("您的从表中有" + count + "个逻辑类型为[主键字段]的字段,请仔细核查!");
		}
    },
    insertMainField: function(){
    	var grid = Ext.getCmp('detail');
    	var field = Ext.getCmp('fo_detailmainkeyfield');
		var count = 0;
		Ext.each(grid.store.data.items, function(){
			var logic = this.data['dg_logictype'];
			var t = this.data['dg_field'];
			if(field.value.toUpperCase() == t.toUpperCase()){
				this.set('dg_logictype', 'mainField');
				logic = 'mainField';
				if(count >= 2){
					if(this.data['dg_id'] == null || this.data['dg_id'] == ''){
						grid.store.remove(this);
					}
				}
			}
			if(logic == 'mainField'){
				count++;
				if(count < 2 && field.value.toUpperCase() != t.toUpperCase()){
					this.set('dg_field', field.value);
				}
				if(count >= 2){
					if(this.data['dg_id'] == null || this.data['dg_id'] == ''){
						grid.store.remove(this);
					}
				}
			}
		});
		if(count == 0){
			grid.store.add({
				dg_sequence: grid.store.data.items[grid.store.data.length-1].data['dg_sequence'] + 1,
				dg_logictype: 'mainField',
				dg_field: field.value,
				dg_caption: 'MainID',
				dg_table: Ext.getCmp('fo_detailtable').value,
				dg_caller: Ext.getCmp('fo_caller').value,
				dg_width: 0,
				dg_visible: '0',
				dg_type: 'text',
				dg_editable: '0',
				dg_dbbutton: '0'
			});
		} else if(count > 1){
			showError("您的从表中有" + count + "个逻辑类型为[关联主表字段]的字段,请仔细核查!");
		}
    },
    createPreForm: function(){
    	var form = Ext.create('Ext.form.Panel', {
    		
    	});
    },
    createPreGrid: function(){
    	
    },
    createFormItem: function(record){
    	
    },
    save: function(){
    	var me = this;
    	var forms = Ext.ComponentQuery.query('myform'), formData = [], added = [], updated = [], deleted = [];
    	if(forms[0].down('field[name=fo_caller]').value==''){
    		showError('CALLER名不能为空');return ;
    	}
    	if(forms[0].down('field[name=fo_title]').value==''){
    		showError('界面名称不能为空');return ;
    	}
    	var flowcaller=forms[0].down('field[name=fo_flowcaller]');
    	if(!flowcaller.value){
			flowcaller.setValue(forms[0].down('field[name=fo_caller]').value);
		}
    	Ext.Array.each(forms, function(form){
    		var grid = form.ownerCt.down('customgrid');
    		var field = form.down('field[name=fo_table]'), val = field.value.split(' ')[0],
    			id = form.dataId || form.down('field[name=fo_id]').value;
    		grid.store.each(function(item){
    			item.set('fd_foid', id);
    			if(item.get('deploy') && !Ext.isEmpty(item.get('fd_field'))){
    				if(Ext.isEmpty(item.get('fd_table'))){
    					item.set('fd_table', val);
    				}
    				if(item.get('fd_field')=='CT_CALLER'){
						item.set('fd_defaultvalue',forms[0].down('field[name=fo_caller]').value);
					}
					if(item.get('fd_field')=='ct_sourcekind'){
						item.set('fd_defaultvalue',forms[0].down('field[name=fo_title]').value);
					}
    			}
    		});
    		formData.push(form.getValues());
    		var dd = grid.getChange(); 
    		added = Ext.Array.merge(added, dd.added);
    		if(!forms[0].down('field[name=fo_id]').value){    			
    			var temp=Ext.Array.pluck(added, 'fd_field');
    			var items =grid.store.data.items,d= null,e = null;
    			var arrayfix = ['ct_id','ct_code','ct_recorder','ct_recorddate','ct_status','ct_statuscode','ct_auditman','ct_auditdate'];  
	    		Ext.each(items, function(item){
	    			d = item.data;
	    			e = grid.removeKey(d, 'deploy');
	    			if(item.get('deploy') && !Ext.isEmpty(item.get('fd_field'))&&Ext.Array.indexOf(arrayfix,item.get('fd_field').toLowerCase(),0)>-1
	    			&&Ext.Array.indexOf(temp,item.get('fd_field'))==-1
	    			){
	    				added.push(e);
	    			}
	    		});
    		}
			updated = Ext.Array.merge(updated, dd.updated);
			deleted = Ext.Array.merge(deleted, dd.deleted);
    	});
		var details = Ext.ComponentQuery.query('mydetail'), gridAdded = [], gridUpdated = [], gridDeleted = [];
		if (details) {
			if(!forms[0].down('field[name=fo_id]').value){
				var g=details[0],items = g.store.data.items,d = null,e = null;
				var arrayfix = ['cd_id','cd_ctid','cd_detno'];  
				Ext.each(items, function(item){
					d = item.data;
					e = g.removeKey(d, 'deploy');
					if(d['deploy'] == true&&Ext.Array.indexOf(arrayfix,d['dg_field'].toLowerCase(),0)>-1) {
						gridAdded.push(e);
					}
				});
			}
			Ext.Array.each(details, function(detail){
				de = detail.getChange();
				gridAdded = Ext.Array.merge(gridAdded, de.added);
				gridUpdated = Ext.Array.merge(gridUpdated, de.updated);
				gridDeleted = Ext.Array.merge(gridDeleted, de.deleted);
			});
		}
		Ext.Array.each(formData, function(d){
			var keys = Ext.Object.getKeys(d);
			for(k in keys) {
				if (keys[k].indexOf('ext-') > -1)
					delete d[keys[k]];
			}
		});
		if(forms[0].down('field[name=fo_id]').value){
			me.onSave(unescape(escape(Ext.encode(formData))), unescape(Ext.encode(added).toString()), 
					unescape(Ext.encode(updated).toString()), unescape(Ext.encode(deleted).toString()), 
					unescape(Ext.encode(gridAdded).toString()), unescape(Ext.encode(gridUpdated).toString()), unescape(Ext.encode(gridDeleted).toString()));
		}else {
			Ext.Ajax.request({
				url : basePath + forms[0].getIdUrl,
				method : 'get',
				async: false,
				callback : function(options,success,response){
					var rs = new Ext.decode(response.responseText);
					if(rs.exceptionInfo){
						showError(rs.exceptionInfo);return;
					}
					if(rs.success){
						formData[0].fo_id=rs.id;
						}
					}
				});
				Ext.Array.each(gridAdded,function(d){
					if(!d['dg_caller']){
						d['dg_caller']=forms[0].down('field[name=fo_caller]').value;
					}
				});
				me.FormUtil.setLoading(true);
				Ext.Ajax.request({
					url : basePath + forms[0].saveUrl,
					params : {
						formStore:unescape(escape(Ext.JSON.encode(formData))),
						param:Ext.encode(added).toString(),
						param2:Ext.encode(gridAdded).toString()
					},
					method : 'post',
					callback : function(options,success,response){
						me.FormUtil.setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.success){
							saveSuccess(function(){
								//add成功后刷新页面进入可编辑的页面 
								var value =formData[0].fo_id,form=forms[0];
								window.location.href = window.location.href +"&formCondition=fo_id="+formData[0].fo_id+"&gridCondition=fd_foid="+formData[0].fo_id+"&whoami="+formData[0].fo_caller;
							});
						} else if(localJson.exceptionInfo){
							var str = localJson.exceptionInfo;
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								saveSuccess(function(){
									//add成功后刷新页面进入可编辑的页面 
									var value =formData[0].fo_id,form=forms[0];
									var formCondition = form.keyField + "IS" + value ;
									var gridCondition = '';
									var grid = Ext.getCmp('grid');
									if(grid && grid.mainField){
										gridCondition = grid.mainField + "IS" + value;
									}
									if(me.contains(window.location.href, '?', true)){
										window.location.href = window.location.href + '&formCondition=' + 
										formCondition + '&gridCondition=' + gridCondition;
									} else {
										window.location.href = window.location.href + '?formCondition=' + 
										formCondition + '&gridCondition=' + gridCondition;
									}
								});
								showError(str);
							} else {
								showError(str);
								return;
							}
						} else{
							saveFailure();//@i18n/i18n.js
						}
					}
				});
			}
    },
    onSave: function(formData, formAdded, formUpdated, formDeleted, gridAdded, gridUpdated, gridDeleted){
		var me = this;
    	me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'ma/updateMultiForm.action',
			params: {
				formData: formData,
				formAdded: formAdded,
				formUpdated: formUpdated,
				formDeleted: formDeleted,
				gridAdded: gridAdded,
				gridUpdated: gridUpdated,
				gridDeleted: gridDeleted
			},
			method : 'post',
			callback : function(opt, s, res){
				me.FormUtil.setLoading(false);
				var rs = new Ext.decode(res.responseText);
				if(rs.success){
					showMessage('提示', '保存成功!', 1000);
					window.location.reload();
				} else if(rs.exceptionInfo){
					var str = rs.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
						str = str.replace('AFTERSUCCESS', '');
						window.location.reload();
					}
					showError(str);return;
				} else {
					showMessage('提示', '保存失败!');
				}
			}
		});
	}
});