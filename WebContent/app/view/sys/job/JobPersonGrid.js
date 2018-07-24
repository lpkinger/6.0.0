Ext.define('erp.view.sys.job.JobPersonGrid',{    
	extend: 'Ext.grid.Panel', 
	alias: 'widget.jobpersongrid',
	id:'saasjobpersongrid',
	columnLines: true,
	viewConfig: {
        stripeRows: true,
        enableTextSelection: true//允许选中文字
    },
	frame: true,
	columns: [
	           { text: '人员姓名', dataIndex: 'EM_NAME'},
	           { text: '岗位名称',  dataIndex: 'EM_POSITION' },
	           { text: '组织名称', dataIndex: 'EM_DEFAULTORNAME' },
	           { text: '人员手机号', dataIndex: 'EM_MOBILE'},
	           { text: '人员账号', dataIndex: 'EM_CODE' },
	           { text: '邮箱', dataIndex: 'EM_EMAIL',flex: 1},
	           
	       ],
	       store:Ext.create('Ext.data.Store',{
				fields:['EM_NAME','EM_POSITION','EM_DEFAULTORNAME','EM_MOBILE','EM_CODE','EM_EMAIL'],
				 proxy: {
				        type: 'memory'
				       /* root:'items'*/
				    }
	       }),
	initComponent : function(){
		/*if(this.autoRender)this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', this.params);*/
		this.callParent(arguments);
	}
})