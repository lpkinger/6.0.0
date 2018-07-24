/**
 * 此toolbar用于明细表grid
 */
Ext.define('erp.view.crm.customermgr.customervisit.Progresstoolbar', {
	extend : 'Ext.Toolbar',
	alias : 'widget.Progresstoolbar',
	dock : 'bottom',
	requires : [ 'erp.view.core.button.AddDetail', 'erp.view.core.button.DeleteDetail', 'erp.view.core.button.Copy',
			'erp.view.core.button.Paste', 'erp.view.core.button.Up', 'erp.view.core.button.Down',
			'erp.view.core.button.UpExcel' ],
	initComponent : function() {
		Ext.apply(this, {// default buttons
			items : [ {
				xtype : 'tbtext',
				id : 'PrDrow'
			}, '-', {
				xtype : 'erpAddDetailButton',
				id : 'PraddDetail'
			}, '-', {
				xtype : 'erpDeleteDetailButton',
				id : 'PrdeleteDetail'
			}, '-', {
				xtype : 'copydetail',
				id : 'PrcopyDetail'
			}, '-', {
				xtype : 'pastedetail',
				id : 'PrpasteDetail'
			}, '-', {
				xtype : 'updetail',
				id : 'PrupDetail'
			}, '-', {
				xtype : 'downdetail',
				id : 'PrdownDetail'
			}, '-' ]
		});
		this.callParent(arguments);
	}
});