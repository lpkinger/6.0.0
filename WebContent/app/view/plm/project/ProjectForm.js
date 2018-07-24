Ext.define('erp.view.plm.project.ProjectForm',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.ProjectForm',
	id: 'projectform', 
   // region: 'south',
  
    frame : true,
    layout:'fit',
    items: [
	  {   
	    title:'相关文件',
		xtyep:'form',
		id:'attachform',
	    region: 'center',
        frame : true,
        height: 'auto',
        fieldDefaults: {
        labelWidth: 55
        },
        layout: {
        type: 'vbox',//hbox水平盒布局 
        align: 'stretch'  // Child items are stretched to full width子面板高度充满父容器 
        },
        items:[{
		 xtype: 'filefield',
    	fieldLabel: '附件',
    	id: 'attach',
    	name: 'file',
    	msgTarget: 'side',
    	allowBlank: false,
    	buttonText: '浏览..',
    	buttonConfig:{
    	Align:'right'
    	}
		}],
		buttonAlign: 'center',
	   },{
	    title:'审批流程',
         layout:'column',
         defaultType: 'textfield',
             
	   } 
      ],
    
});