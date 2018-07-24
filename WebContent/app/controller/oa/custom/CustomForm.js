Ext.QuickTips.init();
Ext.define('erp.controller.oa.custom.CustomForm', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'oa.custom.CustomForm','oa.custom.CustomGrid','core.form.Panel','core.grid.Panel2','ma.MyGrid', 
   		'core.button.UUListener','core.button.Sync','core.button.FormBook','core.form.CheckBoxGroup',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.button.DeleteDetail',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger',
   		'core.grid.TfColumn','core.button.DbfindButton','core.button.ComboButton', 'core.form.YnField'
   	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			click: function(btn){
    				me.save();
    			}
    		},
    		'erpCloseButton':{
    			beforerender:function(btn){
    				btn.handler=function(){
    					parent.Ext.getCmp('singlewin').close();
    				};
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.update();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('fo_id').value);
    			}
    		},
    		'mygrid':{
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
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addForm', '新增Form', 'jsps/ma/form.jsp');
    			}
    		},
    		/*'checkcolumn[dataIndex=fd_dbfind]': {
    			checkchange: function(cm, rIdx, val){
    				if(val) {
    					var grid = cm.up('grid'), 
    						record = grid.store.getAt(rIdx);
    					grid.selModel.select(record);
    				}
    			}
    		},*/
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
    				var record = btn.ownerCt.ownerCt.selModel.lastSelected;
    				if(record && record.data.fd_type == 'C') {   					
        					btn.comboSet(Ext.getCmp('fo_caller').value, record.data.fd_field,me);
    				}
    			}
    		},
    		/**
    		 * DBFind设置
    		 */
    		'erpDbfindButton': {
    			click: function(btn){
    				var record = btn.ownerCt.ownerCt.selModel.lastSelected;
    				if(record && record.data.fd_dbfind != 'F') {
    					btn.dbfindSetUI(Ext.getCmp('fo_caller').value, record.data.fd_field,me);
    				}
    			}
    		},
    		'erpFormPanel textfield[name=fo_table]': {
    			/*change: function(field){
    				Ext.getCmp('fo_caller').setValue(field.value);
    				var grid = Ext.getCmp('grid'),items = grid.store.data.items;
    				Ext.Array.each(items, function(item){
    					if(Ext.isEmpty(item.data['fd_table'])) {
    						item.set('fd_table', field.value);
    					}
    				});
    				field.setValue(field.value.toUpperCase());
    			}*/
    		},
    		'erpFormPanel textfield[name=fo_detailtable]': {
    			change: function(field){
    				field.setValue(field.value.toUpperCase());
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
    		'multidbfindtrigger[name=fd_field]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['fd_table'];
    				if(code == null || code == ''){
    					showError("请先选择表单!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "ddd_tablename='" + code + "'";
    				}
    			},
    			aftertrigger: function(t) {
    				var f = Ext.getCmp('fo_table');
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var a = record.data['fd_table'];
    				if(Ext.isEmpty(f.value) && !Ext.isEmpty(a)) {
    					f.setValue(a.toUpperCase());
    				}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onChange: function(field, value){
		field.setValue(value);
		if(value == 'C'){
			
		}
	},
	save: function(){
		//默认流程CAllER 和页面caller一致;
		var flowcaller=Ext.getCmp('fo_flowcaller');
		if(!flowcaller.value){
			flowcaller.setValue(Ext.getCmp('fo_caller').value);
		}
		Ext.getCmp('fo_detailmainkeyfield').setValue("");
		var grid = Ext.getCmp('grid'),items = grid.store.data.items,added = new Array();
		Ext.each(items, function(item){
			d = item.data;
			if(d['deploy'] == true) {
				if(d['fd_field']=='CT_CALLER'){
					d['fd_defaultvalue']=Ext.getCmp('fo_caller').value;
				}
				if(d['fd_field']=='ct_sourcekind'){
					d['fd_defaultvalue']=Ext.getCmp('fo_title').value;
				}
				added.push(grid.removeKey(d, 'deploy'));
			}
		});
		if(added.length > 0) {
			var form = Ext.getCmp('form');
			this.FormUtil.getSeqId(form);
			//单表界面，清空FORM中从表配置信息
			var formStore=form.getValues();
			formStore.fo_button4rw="erpAddButton#erpUpdateButton#erpDeleteButton#erpSubmitButton" +
					"#erpResSubmitButton#erpAuditButton#erpResAuditButton#erpCloseButton";
			formStore.fo_detailkeyfield='';
			formStore.fo_detailseq='';
			formStore.fo_detailstatus='';
			formStore.fo_detailstatuscode='';
			formStore.fo_detailtable='';
			formStore.fo_detaildetnofield='';
			this.FormUtil.save(formStore, Ext.encode(added));
		} else {
			showError('请至少配置一个有效字段!');
		}
		
	},
	update: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var field = Ext.getCmp('fo_table'), id = Ext.getCmp('fo_id').value;
		Ext.Array.each(items, function(item){
			if(item.data['fd_field'] != null && item.data['fd_field'] != ''){
				item.set('fd_table', field.value);
				item.set('fd_foid', id);
			}
		});
		var me = this;
		if(! me.FormUtil.checkForm()){
			return;
		}
		var dd = grid.getChange();
		me.FormUtil.update(Ext.getCmp('form').getValues(), Ext.encode(dd.added), 
				Ext.encode(dd.updated), Ext.encode(dd.deleted));
	}		
});