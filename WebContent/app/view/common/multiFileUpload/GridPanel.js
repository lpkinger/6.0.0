Ext.define('erp.view.common.multiFileUpload.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpMultiFileUploadGridPanel',
	id: 'fileUploadgrid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store:Ext.create('Ext.data.Store', {
	    fields:['fl_id','fl_name','fl_matchres',
	    		'fl_matchstatus','fl_matchcode','fl_uploadres',
	    		'fl_uploadstatus','fl_deptno'],
	    proxy:{
			type:'ajax',
			async:false, 
			url:'',
			reader:{
				type:'json'
			},
			writer:{
				type:'json'
			}
		},
		autoLoad:false
	}),
	columns:[
		{text: "序号", width:50, sortable: true, dataIndex: 'fl_deptno'},
		{text: "附件名称", flex: 1, sortable: true, dataIndex: 'fl_name'},
		{text: "匹配结果", flex: 1, sortable: true, dataIndex: 'fl_matchres'},
		{text: "上传结果", flex: 1, sortable: true, dataIndex: 'fl_uploadres'}
	],
	initComponent : function(){
		this.callParent(arguments);
	}
});