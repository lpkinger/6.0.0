/**
 * 此toolbar用于明细表grid
 */
Ext.define('erp.view.oa.fee.FeeBacktoolbar', {
	extend : 'Ext.Toolbar',
	alias : 'widget.FeeBacktoolbar',
	dock : 'bottom',
	requires : [ 'erp.view.core.button.AddDetail', 'erp.view.core.button.DeleteDetail', 'erp.view.core.button.Copy',
			'erp.view.core.button.Paste', 'erp.view.core.button.Up', 'erp.view.core.button.Down',
			'erp.view.core.button.UpExcel' ],
	initComponent : function() {
		Ext.apply(this, {// default buttons
			items : [ {
				xtype : 'tbtext',
				id : 'InDrow'
			}, '-', {
				xtype : 'erpAddDetailButton',
				id : 'InaddDetail'
			}, '-', {
				xtype : 'erpDeleteDetailButton',
				id : 'IndeleteDetail'
			}, '-', {
				xtype : 'copydetail',
				id : 'IncopyDetail'
			}, '-', {
				xtype : 'pastedetail',
				id : 'InpasteDetail'
			}, '-', {
				xtype : 'updetail',
				id : 'InupDetail'
			}, '-', {
				xtype : 'downdetail',
				id : 'IndownDetail'
			}, '-' ]
		});
		this.callParent(arguments);
	}
});