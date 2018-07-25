<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript">
function showWin(num){
	var grid=Ext.getCmp('batchDealGridPanel');
	var selmodel=grid.getSelectionModel();
	var record=grid.getStore().getAt(num);
    selmodel.select(record); 
    alert(new Date());
	if(!record) return;
	Ext.create('Ext.window.Window',{
		width:600,
		height:'80%',
		title:'<h1>工单拆分</h1>',
		items:[{
			xtype:'form',
			layout:'column',
			frame:true,
			defaults:{
				xtype:'textfield',
				columnWidth:0.5,
				readOnly:true,
				fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;font-color:blue'
			},
			items:[{
			 fieldLabel:'制造单号',
			 value:record.data.ma_code
			},{
			 fieldLabel:'物料编号'	,
			 value:record.data.ma_prodcode
			},{
			 fieldLabel:'物料名称',
			 value:record.data.ma_prodname
			},{
			 fieldLabel:'订单编号'	,
			 value:record.data.ma_salecode
			},{
		     fieldLabel:'订单序号',
		     value:record.data.ma_saledetno
			},{
			  fieldLabel:'制单数量',
			  value:record.data.ma_qty
			},{
			  fieldLabel:'已完工数',
			  value:record.data.ma_madeqty
			}]
		},{
		  xtype:'gridpanel',
		  title:'分拆批次',
		  id:'grid',
		  iconCls:'x-grid-icon-partition',
		  columnLines:true,
		  plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		        clicksToEdit: 1
		    })],
		  tbar: [{
			    tooltip: '添加批次',
	            iconCls: 'x-button-icon-add',
	            width:25,
	            handler : function() {
	            	var store = Ext.getCmp('grid').getStore();
	                var r = new Object();
	                store.insert(store.getCount(), r);
	            }
	        }, {
	            tooltip: '删除批次',
	            width:25,
	            iconCls: 'x-button-icon-delete',
	            handler: function(btn) {
	                var sm = Ext.getCmp('grid').getSelectionModel();
	                store.remove(sm.getSelection());
	                if (store.getCount() > 0) {
	                    sm.select(0);
	                }
	            },
	            disabled: true
	        }],
		  columns:[{
			  dataIndex:'ma_planbegindate',
			  header:'计划开工日期',
			  xtype:'datecolumn',
			  width:120,
			  editable:true,
			  editor:{
				  xtype: 'datefield',
				  format:'Y-m-d'
			  }
			 
		  },{
			  dataIndex:'ma_planenddate',
			  header:'计划完工日期',
			  xtype:'datecolumn',
			  width:120,
			  editable:true,
			  editor:{
				  xtype: 'datefield',
				  format:'Y-m-d'
			  }
		  },{
			  dataIndex:'wd_planqty',
			  header:'排产数量',
			  width:120,
			  xtype:'numbercolumn',
			  editable:true,
			  editor:{
				  xtype:'numberfield',
				  format:'0',
				  hideTrigger: true
			  }
		  }]
		}]
		
	}).show();
	alert(new Date());
};
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'pm.make.MakePlan'
    ],
    launch: function() {
        Ext.create('erp.view.pm.make.MakePlan');
    }
});
	var caller = getUrlParam('whoami');
	var urlcondition =getUrlParam('urlcondition');
	var page = 1;
	var value = 0;
	var total = 0;
	var dataCount = 0;//结果总数
	var msg = '';
	var height = window.innerHeight;
	if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
		height = screen.height*0.73;
	}
	var pageSize = parseInt(height*0.6/25);
	var keyField = "";
	var pfField = "";
	var url = "";
	var relative = null;	
    var Contextvalue="";
    var LastValue="";
</script>
</head>
<body>
</body>
</html>