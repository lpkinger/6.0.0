Ext.define('erp.view.sys.hr.JprocessGrid',{    
	extend: 'Ext.grid.Panel', 
	//store:Ext.data.StoreMgr.lookup('employeeStore'),
	alias: 'widget.jprocessgrid',
	id:'jprocessgrid',
	//selModel: new Ext.selection.CheckboxModel(),
	//title:'人事资料<span style="color:gray">(所有)</span>',
	columnLines: true,
	viewConfig: {
		stripeRows: true,
		enableTextSelection: true
	},
	forceFit:true,
	dockedItems: [{
		xtype: 'toolbar',
		ui: 'footer',
		items: [{
			xtype:'tbtext',
			text:'<span style="font-weight:bold;" >审批流程定义</span>'
		},{ xtype: 'tbseparator' },{
			text:'载入标准流程',
		    itemId:'loadprocess',
			tooltip:'获取标准流程',
			iconCls:'btn-get'		
		},'-',{
			text:'设计流程',
			href :basePath+'workfloweditor/workfloweditor.jsp',
			tooltip:'添加新记录',
			iconCls:'btn-add'
		},'-',{
			text:'刷新',
			iconCls:'btn-refresh',
			tooltip:'刷新数据',
			handler:function(btn){
				btn.ownerCt.up('grid').getStore().load();
			}
		},'-',{
			text:'帮助',
			iconCls:'btn-help',
			tooltip:'帮助简介'
		}]
	}],
	columns:[/*Ext.create('Ext.grid.RowNumberer',{
		width:35
	}),*/{
		dataIndex:'jd_id',
		width:0,
		text:'ID'
	},{
		dataIndex:'jd_caller',
		width:200,
		text:'页面CALLER'
	},{
		dataIndex:'jd_processdefinitionname',
		width:200,
		flex:1,
		text:'流程名称',
		renderer:function(val, meta, record){
			return '<a href="'+basePath+'jsps/common/jprocessDeploy.jsp?formCondition=jd_idIS'+record.get('jd_id')+'" target="_blank">'+val+'</a>';
		}
	}],
	store:Ext.create('Ext.data.Store',{
		fields:[{name:'jd_id',type:'number'},
		        {name:'jd_caller',type:'string'},
		        {name:'jd_processdefinitionname',type:'string'},
		        {name:'jd_processdefinitionid',type:'string'}],
		        proxy: {
		        	type: 'ajax',
		        	extraParams:{
		        		condition:'1=1'
		        	},
		        	url: basePath+'/common/getProcessInfoByCondition.action',
		        	reader: {
		        		type: 'json',
		        		root: 'data'
		        	}
		        }, 
		        autoLoad: true       
	}),
	initComponent : function(){
		var me=this;
		//me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing')];
		this.callParent(arguments);
	}
});