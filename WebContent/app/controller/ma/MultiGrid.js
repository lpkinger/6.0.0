Ext.QuickTips.init();
Ext.define('erp.controller.ma.MultiGrid', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil'],
    views:[
   		'ma.MultiGrid','ma.MyForm','ma.MyGrid','ma.MyDetail','core.button.DeleteDetail','core.toolbar.Toolbar',
   		'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.grid.TfColumn','core.grid.YnColumn',
   		'core.button.UUListener', 'core.button.DbfindButton','core.button.ComboButton', 'core.form.YnField'
   	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	this.control({ 
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
    				var grid = selModel.view.findParentByType('mygrid');
    				if(record&&record.data.fd_dbfind != 'F'){
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
    				var grid = selModel.view.findParentByType('mydetail');
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
    				//序列号
    				if(Ext.getCmp('fo_seq')) {
    					Ext.getCmp('fo_seq').setValue(Ext.getCmp('fo_table').value.toUpperCase().split(' ')[0] + '_SEQ');
    				}
    				var dt = Ext.getCmp('fo_detailtable');
    				if(dt != null && dt.value != null &&  dt.value != ''){
    					Ext.getCmp('fo_detailseq').setValue(dt.value.toUpperCase().split(' ')[0] + '_SEQ');
    				}
    				var grid = btn.ownerCt.ownerCt;
    				if(Ext.getCmp('fo_table')) {
    					var val = Ext.getCmp('fo_table').value.toUpperCase().split(' ')[0];
        				Ext.Array.each(grid.store.data.items, function(item){
        					if(item.data['fd_field'] != null && item.data['fd_field'] != ''){
        						if(item.data['fd_table'].toUpperCase() != val) {
        							item.set('fd_table', val);
        						}
        					}
        				});
    				}
    				//判断detailtable的主键字段是否加到了detailgrid里面
					//me.insertKeyField();
					//判断detailtable与主表关联的字段是否加到了detailgrid里面
					//me.insertMainField();
    				me.save();
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype:'erpDbfindButton',
    				});
    				btn.ownerCt.add({
    					xtype:'erpComboButton',
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
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
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
    		'dbfindtrigger[name=fo_keyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_codefield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_statusfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_statuscodefield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailkeyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailmainkeyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailstatuscode]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailstatus]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detaildetnofield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'button[name=preview]': {
    			click: function(){
    				
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
				dg_check: 0,
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
				dg_check: 0,
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
    	var form = Ext.getCmp('form');
		var me = this;
		if(form && ! me.FormUtil.checkForm()){
			return;
		}
    	var grids = Ext.ComponentQuery.query('mygrid'), grid;
    	if(grids.length > 0) {
    		var grid = grids[0];
    		if(form) {
	    		var field = Ext.getCmp('fo_table'), val = field.value.split(' ')[0];
	    		grid.store.each(function(item){
	    			if(item.data['fd_field'] != null && item.data['fd_field'] != '' && Ext.isEmpty('fd_table')){
	    				item.set('fd_table', val);
	    			}
	    		});
    		}
    	}
		var details = Ext.ComponentQuery.query('mydetail'), detail;
		if(details.length > 0) {
			var detail = details[0];
			if(form) {
				var field = Ext.getCmp('fo_detailtable'), val = field.value.split(' ')[0];
				detail.store.each(function(item){
					if(item.data['dg_field'] != null && item.data['dg_field'] != '' && Ext.isEmpty('dg_table')){
						item.set('dg_table', val);
					}
				});
			}
		}
		var dd = grid ? grid.getChange() : {},de = detail ? detail.getChange() : {};
		me.onSave(form ? Ext.encode(form.getValues()) : null, Ext.encode(dd.added || []), 
				Ext.encode(dd.updated || []), Ext.encode(dd.deleted || []), Ext.encode(de.added || []), 
				Ext.encode(de.updated || []), Ext.encode(de.deleted || []));
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