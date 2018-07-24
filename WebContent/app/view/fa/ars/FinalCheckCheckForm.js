Ext.define('erp.view.fa.ars.FinalCheckCheckForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.FinalCheckCheck',
	id: 'form', 
//	title: '期末结账检查',
	region: 'center',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
//	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	layout: 'anchor', 
	items: [
	        {	
		
//	    anchor: '100% 30%',
		boxLabel: null,
		checked: false,
		cls: "form-field-allowBlank",
		columnWidth: 0.33333334,
		displayField: null,
		editable: false,
		fieldStyle: "background:#FFFAFA;color:#515151;",
		xtype: 'textfield',
    	fieldLabel: '',
    	allowBlank: false,
    	id: 'labelbuttontext',
    	name: 'labelbuttontext',
    	readOnly :true,
    	value:'期末结账检查'
	
		
		
//        	xtype: 'textfield',
//        	fieldLabel: '',
//        	allowBlank: false,
//        	id: 'text',
//        	name: 'text',
//        	readOnly :true,
//        	value:'期末结账检查'
	}, Ext.create('Ext.grid.Panel', {
		   id : 'grid',
	       emptyText : '无数据',
	       columnLines : true,
	       autoScroll : true,
	       store : new Ext.data.Store({
				
				fields:[
				        
				        {'name':'test1','type':'string'},
				        {'name':'test2','type':'string'},
				        {'name':'test3','type':'string'},
				        {'name':'test4','type':'string'},
				        {'name':'test5','type':'string'}
				        ],
				data:[['','','','',''],['','','','',''],['','','','','']],
				autoLoad:true
				
			}),
	       columns : [
				{'text':'test1','width':100,'cls':'x-grid-header-1','dataIndex':'test1','sortable':false},
				{'text':'test2','width':100,'cls':'x-grid-header-1','dataIndex':'test2','sortable':false},
				{'text':'test3','width':100,'cls':'x-grid-header-1','dataIndex':'test3','sortable':false},
				{'text':'test4','width':100,'cls':'x-grid-header-1','dataIndex':'test4','sortable':false},
				{'text':'test5','width':100,'cls':'x-grid-header-1','dataIndex':'test5','sortable':false}
				
				]
		})
		],
	buttons: [
	          
				{
					name: 'confirm',
					text: $I18N.common.button.erpConfirmButton,
					iconCls: 'x-button-icon-confirm',
					cls: 'x-btn-gray',
					style: {
						marginLeft: '10px'
				    }
				},'-','-',{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					handler: function(){
						var main = parent.Ext.getCmp("content-panel"); 
						main.getActiveTab().close();
					}
				}
				          
	/*{
		xtype: 'erpConfirmButton'
	},'-','-',{
		xtype:'erpCloseButton'
	}*/]
});