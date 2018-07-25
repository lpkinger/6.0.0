Ext.define('erp.view.common.multiFileUpload.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpMultiFileUploadFormPanel',
	id: 'fileUploadform', 
    region: 'north',
    frame: false,
	border: false,
    height:70,
	autoScroll : false,
	buttonAlign : 'left',
	fieldDefaults : {
	       fieldStyle : "background:#FFFAFA;color:#515151;"
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	initComponent : function(){ 
		this.callParent(arguments);
	},
	items:[{
		xtype:'fieldcontainer',
		layout: {
			type:'hbox',
			align:'top',
			pack:'start',
			defaultMargins: { top: 0, right: 0, bottom: 0, left: 8}
		},
		items:[{
        	fieldLabel:'请选择关联的基础数据',
        	labelWidth:150,
        	width:250,
        	id:'datatype',
            xtype: 'combo',
            mode:           'local',
            value:          'mrs',
            triggerAction:  'all',
            forceSelection: true,
            editable:       false,
            displayField:   'name',
            valueField:     'value',
            queryMode: 'local',
    		store: Ext.create('Ext.data.Store', {
		                fields : ['name', 'value'],
		                data   : [
		                    {name : '物料资料',   value: 'product'}
		                 /*   {name : '物料资料',  value: 'product'},
		                    {name : '物料资料', value: 'product'}*/
		                ]
		            })
        },{
        	fieldLabel:'匹配结果',
            xtype: 'displayfield',
            id:'matchres',
         	width: 190,
         	labelWidth:65,
         	value:'成功0条,失败0条'
        },{
        	fieldLabel:'上传结果',
            xtype: 'displayfield',
            id:'uploadres',
            width: 190,
            labelWidth:65,
            value:'成功0条,失败0条'
        }]
	},{
		xtype: 'fieldcontainer',
		layout: {
			type:'hbox',
			align:'top',
			defaultMargins: { top: 0, right: 0, bottom: 0, left: 5}
		},
		items:[{
			xtype: 'form',
        	id:'fileform',
        	iconCls:'x-button-icon-query',
			frame: false,
			border: false,
			layout:'column',
			bodyStyle: {
			    background: '#f1f1f1',
			    padding:0
			},
        	items:[{
	        	xtype:'filefield',
	        	name: 'files',
	        	id:'files',
	        	columnWidth:1,
				buttonText: '选择附件<font color=blue size=1>(≤100M)</font>',
				buttonOnly: true,
				hideLabel: true,
			    createFileInput : function() {
                 var me = this;
                 me.fileInputEl = me.button.el.createChild({
                 name: me.getName(),
                 cls: Ext.baseCSSPrefix + 'form-file-input',
                 tag: 'input',
                 type: 'file',
                 multiple:'multiple',
                 size: 1
               }).on('change', me.onFileChange, me);
              }
			}]
		},{
			text:'一键匹配',
	        xtype: 'button',
         	iconCls:'x-button-icon-match',
         	name:'doMatchData'
		},{
			text:'导出数据',
            xtype: 'button',
            iconCls:'x-button-icon-download',
            hidden:true,
            name:'exportdata'
		},{
			text:'确认上传',
            xtype: 'button',
            iconCls:'x-button-icon-upload',
            name:'uploadConfirm',
            id:'uploadConfirm',
            disabled:true
		},{
			xtype: 'checkbox',
            boxLabel: '只显示未匹配的',
            name:'showNoMatch'
		}]
	}]
});
