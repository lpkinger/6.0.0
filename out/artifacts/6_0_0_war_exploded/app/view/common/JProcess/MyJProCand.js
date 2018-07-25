Ext.define('erp.view.common.JProcess.MyJProCand',{ 
	extend: 'Ext.Viewport', 
	/*layout: 'fit', */
	hideBorders: true, 
	layout:'anchor',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
					title:'',
					  /*  style:'margin: 2px;',
					    contentEl: 'mytask',
					    tag : 'iframe',*/
					anchor: '100% 100%',
					html :'<iframe id="iframe_maindetail_" src="../../jsps/common/datalist.jsp?whoami=JProCand&urlcondition=jp_candidate=\''+em_code+'\' AND jp_flag = 1" height="100%" width="100%" frameborder="0"></iframe>',
				}]
		});
		 me.callParent(arguments); 
	}
});