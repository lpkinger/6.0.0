Ext.define('erp.view.common.DeskTop.MoreSubs', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true, 
	initComponent : function() {
		var me = this;
		Ext.apply(me,{
			items : [{
				anchor : '100% 100%',
				xtype : 'erpDatalistGridPanel',
				forceFit: true,
				keyField:'id_',
				pfField:null,
				url:'',
				id :'Subs',	
				caller :'Subs',	
				firstPage:true,
				defaultCondition :'emp_id_='+em_id,
				showRowNum : false,
				plugins : [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				selModel : null
			}]													
		});
		me.callParent(arguments);
	}

});