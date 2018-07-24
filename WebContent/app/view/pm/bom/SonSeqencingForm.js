Ext.define('erp.view.pm.bom.SonSeqencingForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.SonSeqencing',
	id: 'form', 
	//title: 'BOM子件序号重排作业',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	layout: 'column',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	items: [
			   {		
		    	xtype: 'dbfindtrigger',
		    	fieldLabel: 'BOMID',
		    	allowBlank: true,
		    	id: 'bo_id',
		    	columnWidth: .5,
		    	name: 'bo_id'
			   },
			   {		
		        xtype: 'textfield',
		        fieldLabel: '父件编号',
		        allowBlank: true,
		        id: 'bo_mothercode',
		        columnWidth: .5,
		        name: 'bo_mothercode'
			    },
			    {		
			      xtype: 'textfield',
			      fieldLabel: '父件名称',
			      allowBlank: true,
			      id: 'bo_mothername',
			      columnWidth: .5,
			      name: 'bo_mothername',
			      readOnly: true,
			      fieldStyle: 'background:#f1f1f1;'
				},
				{		
				  xtype: 'textfield',
				  fieldLabel: '规格',
				  allowBlank: true,
				  id: 'bo_spec',
				  columnWidth: .5,
				  name: 'bo_spec',
				  readOnly: true,
				  fieldStyle: 'background:#f1f1f1;'
			    },
			    {		
				  xtype: 'textfield',
				  fieldLabel: '状态',
				  allowBlank: true,
				  id: 'bo_status',
				  columnWidth: .5,
				  name: 'bo_status',
				  readOnly: true,
				  fieldStyle: 'background:#f1f1f1;'
				}
		   ],
	buttons: [{
		xtype: 'erpConfirmButton'
	}]
});