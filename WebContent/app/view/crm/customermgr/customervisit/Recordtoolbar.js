/**
 * 此toolbar用于明细表grid
 */
Ext.define('erp.view.crm.customermgr.customervisit.Recordtoolbar', {
	extend : 'Ext.Toolbar',
	alias : 'widget.recordtoolbar',
	dock : 'bottom',
	requires : [ 'erp.view.core.button.AddDetail', 'erp.view.core.button.DeleteDetail', 'erp.view.core.button.Copy',
			'erp.view.core.button.Paste', 'erp.view.core.button.Up', 'erp.view.core.button.Down',
			'erp.view.core.button.UpExcel' ],
	initComponent : function() {
		Ext.apply(this, {// default buttons
			items : [ {
				xtype : 'tbtext',
				id : 'CuDrow'
			}, '-', {
				xtype : 'erpAddDetailButton',
				id : 'CuaddDetail'
			}, '-', {
				xtype : 'erpDeleteDetailButton',
				id : 'CudeleteDetail'
			}, '-', {
				xtype : 'copydetail',
				id : 'CucopyDetail'
			}, '-', {
				xtype : 'pastedetail',
				id : 'CupasteDetail'
			}, '-', {
				xtype : 'updetail',
				id : 'CuupDetail'
			}, '-', {
				xtype : 'downdetail',
				id : 'CudownDetail'
			}, '-' ]
		});
		this.callParent(arguments);
	}
});