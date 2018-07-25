Ext.QuickTips.init();
Ext.define('erp.controller.common.MoreInfo', {
	extend : 'Ext.app.Controller',
	requires : ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views : ['common.DeskTop.MoreInfo', 'common.datalist.GridPanel',
			'common.datalist.Toolbar', 'core.button.VastAudit',
			'core.button.VastDelete', 'core.button.VastPrint',
			'core.button.VastReply', 'core.button.VastSubmit',
			'core.button.ResAudit', 'core.form.FtField', 'core.grid.TfColumn',
			'core.grid.YnColumn', 'core.trigger.DbfindTrigger',
			'core.form.FtDateField', 'core.form.FtFindField','core.form.BtnDateField',
			'core.form.FtNumberField', 'core.form.MonthDateField','core.grid.HeaderFilter','common.DeskTop.DeskTabPanel'],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');
		this.control({
			'erpDatalistGridPanel' : {
				 itemclick: this.onGridItemClick
			},		
			'#addNews':{
				click: function(){
    				me.FormUtil.onAdd('News-1', '发布新闻', 'jsps/oa/news/News.jsp');
    			}
			},
			'#addNote':{
				click: function(){
    				me.FormUtil.onAdd('Note-1', '发布通知', 'jsps/oa/info/Note.jsp');
    			}
			},
			'#contact':{
				click: function(){
    				me.FormUtil.onAdd('Contact-1', '即时沟通', 'jsps/oa/info/pagingSent.jsp');
    			}
			} 
		});
	},
	onGridItemClick: function(selModel, record){
		Ext.getCmp('desktabpanel').onGridItemClick(selModel, record);
	     }
});