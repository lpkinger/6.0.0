Ext.QuickTips.init();

Ext.define('erp.view.core.form.CancelForm', {
	extend:'erp.view.core.form.Panel',	
	alias:'widget.erpCancelFormPanel',
	id:'form',
	keyField:'pi_id',
	statusField:"pi_status",
	statuscodeField:"pi_statuscode",
	layout:'column',
	title:'作废出入库单 ',
	enableKeyEvents:false,
	formdata:null,
	getItemsAndButtons:function(){
		var param = {caller: caller, condition: 'pi_id='+getUrlParam('pi_id'), _noc: (getUrlParam('_noc'))};
		this.FormUtil.getItemsAndButtons(this, 'scm/getCancelFormItems.action', param);
	}
});
