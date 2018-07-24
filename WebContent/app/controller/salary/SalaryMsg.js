Ext.define('erp.controller.salary.SalaryMsg', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil','erp.util.GridUtil',"erp.view.core.form.MonthDateField"],
    views: ['salary.SalaryMsg'],
    init: function(){ 
    	var me=this;
    	this.control({
    		'#salaryMsg':{
    			afterrender:function(p){
    				var store=Ext.create("Ext.data.Store",{
    					fields:["SL_NAME","SL_DATE","SL_MESSAGE","SL_STATUS","SL_NOTEDATE","SL_TYPE"],
    					pageSize:20,
    					proxy:{
    						type:'ajax',
    						url:basePath+'salaryMsg/getMessgeLog.action',
    						reader:{
    							type:'json',
    							root:'logs',
    							totalProperty:'num',
    							}
    					},
    					autoLoad:true,
    				});
    				p.add({
    					xtype:"grid",
    					id:'data',
    					//margin:"20 0 0 0",
    					columnLines: true,
    					bodyStyle:{
    						background:'#ffffff'
    					},
    					store:store,
    					plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
    					selModel:Ext.create('Ext.selection.CheckboxModel',{
    						headerWidth: 0,
    					}),
    					dockedItems:[{
    						xtype:"pagingtoolbar",
    				        dock: 'bottom',
    				        store:store,
    				        emptyMsg:"数据为空",
    				        displayMsg:"显示第{0}-{1}行数,总共{2}行",
    				        displayInfo: true
    					}],
    					anchor:"100% 100%",
    					columns:{},
    				});
    				var grid=Ext.getCmp("data");
    				grid.reconfigure(store, me.createColumns());
    			}
    		}
    	});
    },
    createColumns:function(){
    	var arr=[{
			xtype: 'rownumberer',
			width: 35,
			align:'cener',
		},{
			text:'姓名',
			dataIndex:"SL_NAME",
			width:100,
			height:30,
		},{
			text:'状态',
			dataIndex:"SL_STATUS",
			width:100,
			height:30,
		},{
			text:'薪资月份',
			dataIndex:"SL_NOTEDATE",
			width:100,
			height:30,
			renderer:function(val){
				if(val){
    				var date=new Date(val);
    				return Ext.Date.format(date,'Y年m月');		
				}
			}
		},{
			text:'工资类型',
			dataIndex:"SL_TYPE",
			width:70,
			height:30,
		},{
			text:'内容',
			dataIndex:"SL_MESSAGE",
			width:500,
			renderer:function(val){
				if(!val){
					return '<font style="color:#00ffc6; font-weight:bold">由于不想写,所以没有留言!</font>';
				}else return val;
			}
		},{
			text:'时间',
			dataIndex:"SL_DATE",
			width:180,
			renderer:function(val){
				if(val){
					var d=new Date(val);
					var s=d.getFullYear()+'-'+(d.getMonth()+1)+'-'+d.getDate()+' '+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds();
					return s;
				}
			}
		}];
    	Ext.each(arr,function(dd){
    		if(dd.xtype!='rownumberer'){
    			dd.filter= {
 			         dataIndex: dd.dataIndex,
 			         xtype: "textfield",
 			      };
 				dd.filterJson_={};
    		}
    	});
    	
    	return arr;
    }
});