Ext.define('erp.view.common.datalistFilter.TreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.FilterTreePanel',
	id:'TreePanel',
	title:'<span style="color:#000;font-weight:bold">查询方案</span>',
	rootVisible:false,
	autoScroll:true,
	containerScroll : true,
	cls:'TreePanel',
	root:{expanded: true,       
		 children: [{ text: "标准方案", leaf: false,expanded:true},
		           { text: "自定义方案", leaf: false,expanded:true }
		           ]},
   initComponent : function(){
			this.callParent(arguments); 	
   }   
});