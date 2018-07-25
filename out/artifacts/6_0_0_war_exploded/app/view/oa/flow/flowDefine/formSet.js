Ext.QuickTips.init();

Ext.define('erp.view.oa.flow.flowDefine.formSet', {
	extend:'Ext.form.Panel',
	alias:'widget.formSet',
	layout:'border',
	id:'formSet',
	cls:'form',
	items: [{
		xtype: 'erpFormPanel', 
		region: 'north',
		saveUrl: 'ma/saveForm.action',
		deleteUrl: 'ma/deleteForm.action',
		updateUrl: 'ma/updateForm.action',
		getIdUrl: 'common/getId.action?seq=FORM_SEQ',
		keyField: 'fo_id',
		_nobutton: true,
		autoScroll:true,
		height:'25%',
		enableTools:false
	},{
		title:'字段设置',
		xtype: 'formGrid', 
		id: 'formGrid',
		region: 'center',
		detno: 'fd_detno',
		necessaryField: 'fd_field',
		keyField: 'fd_id',
		mainField: 'fd_foid'
	}]
});
