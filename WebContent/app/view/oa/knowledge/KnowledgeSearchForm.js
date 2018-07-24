Ext.define('erp.view.oa.knowledge.KnowledgeSearchForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpKnowledgeSearchFormPanel',
	id: 'searchform', 
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
	       
	       labelAlign : "left",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	items:[
	{
	  columnWidth:'0.3',
	  fieldLabel:'全文检索',
	  name:'search',
	  id:'search',
	  xtype:'textfield',
	  fieldStyle : 'background:#DEDEDE',
	}, {
       xtype: 'button',
       iconCls : 'prev',
       //text:'上一行',
       scale : "medium" ,
       id:'previous',
        width: 24,
      style:'margin-left:20px; background:#CDCDB4; height:24px;width:24px; border: 0px  ;text-decoration: none ;'
    }, {
       xtype: 'button',
      // text:'下一行',
       iconCls : 'next',
       scale : "medium" ,
       id:'next',
       width: 24,
      style:'margin-left:20px;background:#CDCDB4; height:24px; border: 0px  ;text-decoration: none ;'
    }],
	initComponent : function(){ 
		this.callParent(arguments);
	},
});