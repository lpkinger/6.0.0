Ext.QuickTips.init();
Ext.define('erp.controller.oa.flow.FlowDefine', {
	extend : 'Ext.app.Controller',
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : [ 'oa.flow.flowDefine.viewport','oa.flow.flowDefine.flowDefineform','oa.flow.flowDefine.formSet',
	          'core.trigger.TextAreaTrigger','core.form.Panel','oa.flow.flowDefine.formGrid','core.grid.Panel2',
	          'erp.view.core.form.FileField','core.button.Add','core.button.Save','core.button.Close','core.button.Update',
	          'core.button.Delete','core.button.DeleteDetail','core.trigger.TextAreaTrigger','erp.view.core.trigger.DbfindTrigger',
	          'core.toolbar.Toolbar','erp.view.core.trigger.MultiDbfindTrigger','core.grid.TfColumn','core.button.DbfindButton',
	          'core.button.ComboButton', 'core.form.YnField','core.form.MultiField','core.trigger.AddDbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
			'field[id=fo_caller]':{
				afterrender:function(f){
					if(flowCaller){
						f.setReadOnly(true);
					}
				}
			},
			'erpFormPanel':{
				afterload:function(p){
					Ext.Array.each(p.items.items, function(item){
						if(item.title!='主表资料'&&item.group!=1){
							item.hide()
						}else{
							if(item.group!=0&&'fo_table#fo_caller#fo_title#fo_keyfield#fo_codefield#fo_seq'.indexOf(item.id)<0){
								item.hide()
							}
						}
					});
				}
			},
			'button[id=saveFlowDefine]':{
				click:function(){
					me.save(me);
				}
			},
			'button[id=updateFlowDefine]':{
				click:function(){
					me.update();
				}
			},
			'formGrid':{
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    				var grid = selModel.view.ownerCt.ownerCt;
    				if(record && record.data.fd_dbfind !='F') {
    					grid.down('erpDbfindButton').setDisabled(false); 
    					grid.down('erpComboButton').setDisabled(true);
    				}else if(record && record.data.fd_type == 'C') {
    					grid.down('erpDbfindButton').setDisabled(true); 
    					grid.down('erpComboButton').setDisabled(false);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    					grid.down('erpDbfindButton').setDisabled(true);
    				}
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
    				var grid = btn.ownerCt.ownerCt, record = grid.selModel.lastSelected;
    				if(record && record.data.fd_dbfind != 'F') {
    					btn.dbfindSetUI(Ext.getCmp('fo_caller').value, record.data.fd_field, grid);
    				}
    			}
    		}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	save: function(me){
		//保存flow_define
		var params = {};
		params.name = Ext.getCmp('fd_name').value;
		params.shortname = Ext.getCmp('fd_shortname').value;
		params.remark = Ext.getCmp('fd_remark').value;
		params.defaultCode = Ext.getCmp('fd_defaultdutycode').value;
		params.PrefixCode = Ext.getCmp('PrefixCode').value;
		if(!Ext.getCmp('fo_caller').value){
			showError('请填写caller字段');
		}
		params.caller = Ext.getCmp('fo_caller').value;
		Ext.Ajax.request({
			async:false,
			url : basePath + '/oa/flow/saveDefine.action',
			params: params,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
			}
		});
		//保存form设置
		//序列号
		Ext.getCmp('fo_seq').setValue(Ext.getCmp('fo_table').value.toUpperCase() + '_SEQ');
		var dt = Ext.getCmp('fo_detailtable').value;
		if(dt != null && dt != ''){
			Ext.getCmp('fo_detailseq').setValue(dt.toUpperCase() + '_SEQ');
			var dm = Ext.getCmp('fo_detailmainkeyfield').value;
			if(dm == null || dm == ''){
				showError("请选择从表与主表关联的字段!");return;
			}
		}
		var grid = Ext.ComponentQuery.query('formGrid')[0], items = grid.store.data.items, dd = new Array(), d = null;
		var field = Ext.getCmp('fo_table');
		var count = 0;
		Ext.Array.each(items, function(item){
			d = item.data;
			if(!Ext.isEmpty(d['fd_field'])){
				if(Ext.isEmpty(d['fd_table']))
						item.set('fd_table', field.value);
				d.fd_readonly = d.fd_readonly ? 'T' : 'F';
				d.fd_dbfind = d.fd_dbfind ? d.fd_dbfind : 'F';
				d.fd_allowblank = d.fd_allowblank ? 'T' : 'F';
				d.fd_modify = d.fd_modify ? 'T' : 'F';
				d.fd_check = d.fd_check ? 1 : 0;
				dd.push(d);
			}
			if(item.get('fd_logictype') && !Ext.isEmpty(item.get('fd_logictype'))){
				if(item.get('fd_logictype')=='title'){
					count++;
				}
			}
		});
		if(count==0){
			showError('请至少配置一个标题字段（逻辑类型为：title）!');
			return false;
		}
		if(dd.length > 0) {
			var form = Ext.getCmp('form');
			this.FormUtil.getSeqId(form);
			me.onSave(form.getValues(), Ext.encode(dd),params.caller);
		} else {
			showError('请至少配置一个有效字段!');
		}
	},
	update: function(){
		//更新flow_define
		var params = {};
		params.name = Ext.getCmp('fd_name').value;
		params.shortname = Ext.getCmp('fd_shortname').value;
		params.remark = Ext.getCmp('fd_remark').value;
		params.defaultCode = Ext.getCmp('fd_defaultdutycode').value;
		params.PrefixCode = Ext.getCmp('PrefixCode').value;
		params.caller = flowCaller;
		Ext.Ajax.request({
			async:false,
			url : basePath + '/oa/flow/updateDefine.action',
			params: params,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
			}
		});
		//更新form设置
		var grid = Ext.ComponentQuery.query('formGrid')[0];
		var field = Ext.getCmp('fo_table'), id = Ext.getCmp('fo_id').value;
		var count =0;
		grid.store.each(function(item){
			if(item.get('deploy') && !Ext.isEmpty(item.get('fd_field'))){
				if(item.get('fd_foid') != id)
					item.set('fd_foid', id);
				if(Ext.isEmpty(item.get('fd_table')))
					item.set('fd_table', field.value);
			}
			if(item.get('fd_logictype') && !Ext.isEmpty(item.get('fd_logictype'))){
				if(item.get('fd_logictype')=='title'){
					count++;
				}
			}
		});
		if(count==0){
			showError('请至少配置一个标题字段（逻辑类型为：title）!');
			return false;
		}
		var me = this;
		if(! me.FormUtil.checkForm()){
			return;
		}
		var dd = grid.getChange();
		
		me.FormUtil.update(Ext.getCmp('form').getValues(), Ext.encode(dd.added), 
				Ext.encode(dd.updated), Ext.encode(dd.deleted));
	},
	onSave: function(){
		var flowCaller = arguments[2];
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.param = unescape(arguments[1].toString());
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			if(arguments[i])
				params['param' + i] = unescape(arguments[i].toString());
		}  
		var me = this;
		var form = Ext.getCmp('form'), url = form.saveUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		}
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			async:false,
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					saveSuccess(function(){
						//add成功后刷新页面进入可编辑的页面 
						var value =r[form.keyField];
						var formCondition = "fo_idIS" + value ;
						var gridCondition = "fd_foidIS" + value ;
//						window.location.href = window.location.href + '?flowcaller='+ flowCaller + '&formCondition=' + 
//						formCondition + '&gridCondition=' + gridCondition;
					});
				} else{
					saveFailure();//@i18n/i18n.js
				}
			}

		});
	}
});