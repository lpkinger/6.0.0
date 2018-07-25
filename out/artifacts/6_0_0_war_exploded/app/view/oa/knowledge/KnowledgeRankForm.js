Ext.define('erp.view.oa.knowledge.KnowledgeRankForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpKnowledgeRankFormPanel',
	id: 'rankform', 
    region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	items:[
	{
	 columnWidth:'1',
	  fieldStyle : 'background:#CDCDB4 ;border-bottom-style:1px solid;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CDCDB4;border-bottom-style:none;border-left:none; ',
	}
	,{
	   columnWidth:'0.2',
	   fieldLabel:'知识排行',
	   id:'rankfield',
	   labelStyle:'font-size:16px;font-weight: bold;',
	   fieldStyle : 'background:#CDCDB4 ;border-bottom-style:1px solid;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none;font-weight: bold; ',
	},{
	  columnWidth:0.1,
	  html:'<div></div>'
	},	
	{
	   columnWidth:'0.1',
	   html:'<div id="sidebar"><a href="javascript:order(1);"  style="text-decoration: none||blink;font-size:16px;text-align:left;font-weight: bold; ">最新知识</a></div>',
	},{
	  columnWidth:'0.1',
	  html:'<div id="sidebar"><a href="javascript:order(2);"  style="text-decoration: none||blink;font-size:16px; font-weight: bold;">热门点击</a></div>',
	},{
	   columnWidth:'0.1',
	   html:'<div id="sidebar"><a href="javascript:order(3);"  style="text-decoration: none||blink;font-size:16px; font-weight: bold;">强力推荐</a></div>',
	},{
	   columnWidth:'0.1',
	   html:'<div id="sidebar"><a href="javascript:order(4);"  style="text-decoration: none||blink;font-size:16px;font-weight: bold; ">最佳知识</a></div>',
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	},
	order:function(ind){
	 alert('ind');
	
	}
});