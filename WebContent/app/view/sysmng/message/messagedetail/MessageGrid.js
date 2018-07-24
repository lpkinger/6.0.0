
Ext.define('erp.view.sysmng.message.messagedetail.MessageGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.MessageGrid',
	id: 'gridpanel', 
	closeAction:'hide',
	layout: 'fit',
	border:false,	
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
    bbar: {xtype: 'erpToolbar', dock: 'bottom', 
    		enableAdd: false, enableDelete: true, 
    		enableCopy: false, enablePaste: false, 
    		enableUp: false, enableDown: false,
    		enableExport : false},
	plugins:[  
         Ext.create('Ext.grid.plugin.CellEditing',{  
         clicksToEdit:1 //设置单击单元格编辑  
		})  
     ], 
	columns:[
		{
			dataIndex:'mr_id',
			hidden:true,
			header:'id',
			id : 'Mr_id',
			
		},
		{
			dataIndex:'mr_mmid',
			hidden:true,
			header:'关联列表ID',
			id : 'Mr_mmid',
			
		},
		
		{
			dataIndex:'mr_desc',
			header:'角色描述',
			id : 'Mr_desc',
			//sortable: true,
			flex: 1,
			editor : {
    			xtype : 'textfield'
			}
			
		},
		{
			dataIndex:'mr_ispopwin',
			header:'强制弹窗',
			id : 'Mr_ispopwin',
			flex: 1,
			editor : {
    			xtype : 'combo',
    			editable:false,
				displayField:'name',			
				valueField: 'value',       
    			store: Ext.create('Ext.data.Store', {
                    	fields : ['name', 'value'],
                   	 data   : [
                        {name : '否',   value:'0'},
                        {name : '是',   value:'-1'}
                    ]
             })
             },
			renderer : function(val, meta, record) {
						
							if(val==-1){
								return "是"								
							}else{
								return "否"
							}
						}
			
		},
		{
			dataIndex:'mr_iscombine',
			header:'消息合并',
			id : 'Mr_iscombine',
			flex: 1,			
			editor : {
    			xtype : 'combo',
    			editable:false,
				displayField:'name',			
				valueField: 'value', 				
    			store: Ext.create('Ext.data.Store', {
                    	fields : ['name', 'value'],
                   	 data : [                       
                        {name : '是',   value:'-1'},
                        {name : '否',   value:'0'}
                    ]
             }),           
             listeners:{
						change:function(comb,val) { // 设置下拉框默认值
							
						}
             }
			},			
			renderer : function(val, meta, record) {
							if(val==-1){
								return "是"								
							}else if(val==0){
								return "否"
							}
						}
		
				
		},
		{
			dataIndex:'mr_combinecond',
			header:'合并条件',
			flex: 1,
			id : 'Mr_combinecond',
			editor : {
    			xtype : 'textareatrigger',
			}
		},
		{
			dataIndex:'mr_level',
			header:'消息等级',
			flex: 1,
			id : 'Mr_level',
			editor : {
    			xtype : 'combo',
    			editable:false,
				displayField:'name',			
				valueField: 'value',       
    			store: Ext.create('Ext.data.Store', {
                    	fields : ['name', 'value'],
                   	 data   : [
                        {name : '中',   value:'中'},
                        {name : '低',   value:'低'},
                        {name : '高',   value:'高'}  
                    ]
             })
             }
		},
		{
			dataIndex:'mr_messagestr',
			header:'知会消息详情',
			flex: 1,
			id : 'Mr_messagestr',
			
			editor : {
    			xtype : 'HtmlEditorTrigger',
    			editable:false,
			}
		},
		{
			dataIndex:'mr_messagedemo',
			header:'知会消息样例',
			flex: 1,
			id : 'Mr_messagedemo',
			editor : {
    			xtype : 'textfield'
			}
		},
		{
			dataIndex:'mr_wintype',
			header:'消息弹窗模板',
			flex: 1,
			id : 'Mr_wintype',
			editor : {
    			xtype : 'combo',
    			editable:false,
				displayField:'name',			
				valueField: 'value',       
    			store: Ext.create('Ext.data.Store', {
                    	fields : ['name', 'value'],
                   	 data   : [
                        {name : '普通知会信息',   value:'普通知会信息'},
                        {name : '审批知会信息',   value:'审批知会信息'},
                        {name : '任务知会信息',   value:'任务知会信息'}
                        
                    ]
             })
             }
		},
		{
			dataIndex:'mr_sql',
			header:'SQL语句',
			flex: 1,
			id : 'Mr_sql',	
			editable:false,
			editor : {
    			xtype : 'textareatrigger',
    			
			}
		},
		{
			dataIndex:'mr_mans',
			hidden:true,
			flex: 1,
			header:'固定知会人',
			id : 'Mr_mans',
			editor : {
    			xtype : 'textfield'
			}
			
		},
		{
			dataIndex:'mr_manids',
			hidden:true,
			flex: 1,
			header:'固定知会人id',
			id : 'Mr_manids',
			editor : {
    			xtype : 'textfield'
			}
			
		},
		{
			dataIndex:'mr_isused',
			header:'启用状态',
			flex: 1,
			id : 'Mr_isused',
		
			renderer : function(val, meta, record) {
							
							if(val==-1){
								return "是"								
							}else{
								return "否"
							}
						},
			editor : {
    			xtype : 'combo',
    			editable:false,
				displayField:'name',			
				valueField: 'value',       
    			store: Ext.create('Ext.data.Store', {
                    	fields : ['name', 'value'],
                   	 data   : [
                        {name : '是',   value:'-1'},
                        {name : '否',   value:'0'}
                       
                        
                    ]
             })
             }
			
		}
		
	
		],
	store: Ext.create('Ext.data.Store', {
    	fields: [{
        	name:'mr_id',
        	type:'number'
        }, {
        	name:'mr_mmid',
        	type:'number'
        },{
        	name:'mr_sql',
        	type:'string'
        },{
        	name:'mr_desc',
        	type:'string'
        },{
        	name:'mr_ispopwin',
        	type:'number'
        },{
        	name:'mr_iscombine',
        	type:'number'
        },{
        	name:'mr_combinecond',
        	type:'string'
        },{
        	name:'mr_level',
        	type:'string'
        },{
        	name:'mr_messagestr',
        	type:'string'
        },
        {
        	name:'mr_messagedemo',
        	type:'string'
        },
        {
        	name:'mr_wintype',
        	type:'string'
        },
        {
        	name:'mr_isused',
        	type:'number'
        },
        {
        	name:'mr_mans',
        	type:'string'
        },
        {
        	name:'mr_manids',
        	type:'string'
        }
        ]}),

	initComponent : function(){ 
		
		this.callParent(arguments);
		this.getGridData();
		
	},
	getGridData:function(){		
		var me = this;
		//从url解析参数
		 formCondition = getUrlParam('formCondition');
		if(formCondition != null && formCondition != ''){
			formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=").replace(/\'/g,"");
			this.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'sysmng/getMessageGridData.action',
	        	params: {	        		 
	        		id: formCondition.split("=")[1]	       
	        	},
	        	method : 'post',
	        	async:false,
	        	callback : function(options,success,response){	        		
	        		me.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}else{
	        			
	        			var grid=Ext.getCmp('gridpanel');
	        			me.store.loadData(res.data);
	        			//grid.store.loadData(res.data);        			
	        		}
	        	}
	        });
	}},
	
	
	
	
});