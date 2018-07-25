Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.ProjectWeekPlan', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.task.ProjectWeekPlan','plm.task.ProjectWeekPlanTree','common.batchDeal.Viewport','common.batchDeal.Form','common.batchDeal.GridPanel','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
	       'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton','core.button.CheckVendorUU',
	       'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger',
	       'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV','core.button.Update','core.button.Close'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'erpBatchDealGridPanel':{
	    			   beforerender:function(grid){
	    				   var column = null;
	    				   Ext.Array.each(grid.columns,function(item,index){       					
	    					   item.editor = null; //取消列的可编辑
								if(item.dataIndex=='wrd_mileplan'||item.dataIndex=='wrd_finishedtask'||item.dataIndex=='wrd_summary'||
    	   							item.dataIndex=='wrd_nextplan'||item.dataIndex=='wrd_difficulty'){	
    	   								item.renderer = function(val, meta, record, x, y, store, view){	
		    	   							var val = val.replace(/\n/g,'</br>');
		    	   							return val;
	   								}
    	   						}
	    					   if(item.dataIndex=='turned'){
	    						   column = item;
	    					   }
	    				   });
	    				   if(column){
	    					   Ext.Array.remove(grid.columns,column); //移除是否已暂存列
	    				   }

	    				   var bbar = null;
	    				   Ext.Array.each(grid.dockedItems.items,function(item,index){
	    					   if(item.xtype=='erpToolbar'){
	    						   bbar = item;
	    					   }
	    				   });
	    				   var actionColumn = Ext.create('Ext.grid.column.Action',{
	    					   cls:'x-grid-header-1',
	    					   width : 40,
	    					   items : [{
	    						   icon : basePath + 'resource/images/16/edit.png',
	    						   tooltip : '更新',
	    						   handler : function(grid, rowIndex, colIndex) {
	    							   var record = grid.getStore().getAt(rowIndex);
	    							   me.showWin(record);
	    						   }
	    					   }]
	    				   });      		
	    				   grid.down('erpExportDetailButton').hide(); //隐藏下载模板和导入明细数据按钮
	    				   
	    				   grid.headerCt.insert(1,actionColumn); //插入actioncolumn


	    			   },
	    			   afterrender:function(grid){
	    				   Ext.Array.remove(grid.headerCt.items.items,grid.headerCt.items.items[0]);
	    				   grid.store.removeAll();
	    			   }
	    		   },
	    		   'erpBatchDealFormPanel':{
	    			   beforerender:function(form){
	    				   var tbar = null;
	    				   var exportBtnAndSeparator = new Array(); //移除导出按钮和分隔符
	    				   Ext.Array.each(form.dockedItems.items,function(item,index){
	    					   if(item.xtype=='toolbar'){
	    						   tbar = item;
	    					   }
	    				   });
	    				   if(tbar){
	    					   Ext.Array.each(tbar.items.items,function(item,index){
	    						   if(item.id=='export'||item.xtype=='tbseparator'||item.id=='close'){
	    							   exportBtnAndSeparator.push(item);
	    						   }
	    					   });

	    					   if(exportBtnAndSeparator){
	    						   Ext.Array.each(exportBtnAndSeparator,function(item){
	    							   Ext.Array.remove(tbar.items.items,item);
	    						   });    						
	    					   }

	    				   }       			
	    			   },
	    			   alladded:function(form){
	    				   var weekdate = Ext.getCmp('wr_date');
	    				   var startdate = Ext.getCmp('prj_start');
	    				   var enddate = Ext.getCmp('prj_end');

	    				   var combo = weekdate.items.items[0];
	    				   combo.store.loadData([{
	    					   display:'本周',
	    					   value:-9	        				
	    				   }],true);

	    				   //将周报日期默认设为本周
	    				   var comboEl = weekdate.items.items[0].inputEl;
	    				   Ext.EventManager.on(comboEl,'select',function(combo){
	    					   if(combo.target.value=='本周'){
	    						   me.setComboTime();
	    					   }	        				
	    				   });

	    				   combo.select(combo.store.getAt(combo.store.getCount()-1));
	    				   me.setComboTime();

	    				   //计划开始、结束日期默认设为为自定义	
	    				   if(startdate){
	    					   startdate.setDateFieldValue(7);
	    					   startdate.items.items[0].setValue('自定义');	     					
	    				   }
	    				   if(enddate){
	    					   enddate.setDateFieldValue(7);
	    					   enddate.items.items[0].setValue('自定义');	     					
	    				   }
	    				   form.onQuery();
	    			   }
	    		   },
	    		   'erpProjectWeekPlanTree':{
	    			   beforeselect:function(tree,record,index){
	    				   var prjname = Ext.getCmp('prj_name');
	    				   var prjassignto = Ext.getCmp('prj_assignto');
	    				   var producttype = Ext.getCmp('prj_producttype');
	    				   var prjclass = Ext.getCmp('prj_class');
	    				   if(prjname){
	    					   prjname.setValue(null);
	    				   }
	    				   if(producttype){
	    					   producttype.setValue(null);
	    				   }
	    				   if(prjassignto){
	    				   	   prjassignto.setValue(null);
	    				   }
	    				   if(prjclass){
	    				   	   prjclass.setValue(null);
	    				   }
	    			   },
	    			   beforeitemmousedown:function(treeview,record){        	
	    				   var tree = treeview.ownerCt;
	    				   var currentNode = tree.getSelectionModel().getSelection()[0];

	    				   if(!currentNode){  //第一次点击
	    					   currentNode = record;
	    					   currentNode.selected = true;
	    				   }else{
	    					   if(typeof(record.selected)!='undefined'){
	    						   if(currentNode.id==record.id){
	    							   currentNode.selected = !currentNode.selected;
	    						   }else{
	    							   record.selected = true;
	    							   currentNode.selected = false;
	    						   }
	    					   }else{
	    						   record.selected = true;
	    						   currentNode.selected = false;
	    					   }

	    				   }
	    				   var node = treeview.getNode(record);
	    				   var el = Ext.get(node);
	    				   el.dom.style.color = 'red!important';
	    				   tree.fireEvent('itemmousedown',treeview,record);
	    			   },
	    			   select:function(tree,record,index){    				
	    				   if(record.data.leaf){
	    					   var type = record.get('type');
	    					   var prjname = Ext.getCmp('prj_name');
	    					   var producttype = Ext.getCmp('prj_producttype');
	    					   if(type){
	    						   if('product'==type){
	    							   if(producttype){
	    								   producttype.setValue(record.get('name'));
	    							   }
	    						   }else if('project'==type){
	    							   if(prjname){
	    								   var name = record.get('name');
	    								   name = name.substring(0,name.indexOf('</span>'));
	    								   name = name.substring(name.lastIndexOf('>')+1,name.length);
	    								   prjname.setValue(name);   
	    								   prjname.autoDbfind('form','ProjectWeekly!Deal','prj_name',"prj_name='"+name+"'");
	    							   }
	    						   }			
	    					   }	
	    				   }else{
	    					   var grid = Ext.getCmp('batchDealGridPanel');
	    					   me.product = '';
	    					   me.getProjectCondition(record,me);
	    					   if(''!=me.product){
	    						   me.product = me.product.substring(1);
	    						   grid.defaultCondition = 'prj_producttype in (' + me.product + ')';
	    					   }
	    				   }
	    			   }
	    		   },
	    		   'gridcolumn[dataIndex=wrd_prjname]':{
	    			   beforerender:function(col){
		    			   col.renderer=function(val,meta,record){
		    				  return  Ext.String.format('<p style="font-weight:600;">标题: {0}</p><p>责任人: {1}</p><p>预计完成日期: {2}</p><p style="font-weight:600;">状态: {3}</p>  ',
		   							record.get('wrd_prjname'),
		   							record.get('prj_assignto'),
		   							record.get('prj_end')!=null ?Ext.Date.format(record.get('prj_end'),'Y-m-d'):' ',
		   						    record.get('wrd_prjstatus')
		   					  );
		    			   }
	    			   }

	    		   },
	    		   'erpCloseButton':{
	    		   	   afterrender:function(){
	    		   console.log('closebtn');
	    		   	   },
	    		   	   click:function(btn){
	    		   	   	console.log(btn);
	    		   	   }
	    		   }
	    	   });
	       },
	       getProjectCondition:function(record,me){
	    	   if(record.data.type=='product'){
	    		   me.product += ",'" + record.data.name + "'";
	    	   }
	    	   for(var i=0;i<record.childNodes.length;i++){
	    		   me.getProjectCondition(record.childNodes[i],me);
	    	   }
	       },
	       showWin:function(record){
	    	   var me = this;
	    	   var changeWin = Ext.getCmp('changeWin');
	    	   if(changeWin){
	    		   changeWin.show();
	    	   }else{
	    		   var win = Ext.create('Ext.window.Window', {
	    			   title : '修改',
	    			   height : '35%',
	    			   width : '42%',
	    			   border:false,
	    			   layout:'fit',
	    			   closeAction:'hide',
	    			   id:'changeWin',
	    			   modal:true,
	    			   items:[{
	    				   xtype : 'form',
	    				   layout : 'column',
	    				   id : 'updateWin',
	    				  // bodyStyle : 'background:#F2F2F2;',
	    				   border:false,
	    				   defaults : {
	    					   columnWidth:0.5,
	    					   margin:'5 5 5 5'
	    				   },
	    				   items : [{
	    					   xtype : 'textfield',
	    					   name:'wrd_prjcode',
	    					   fieldLabel : '项目编号',
	    					   border : false,
	    					   readOnly : true,
	    					   readOnlyCls : 'win-textfield-readOnly',
	    					   fieldStyle : 'background:#d6d6d6'
	    				   },{
	    					   xtype : 'textfield',
	    					   name : "wrd_prjname",
	    					   fieldLabel : '项目名称',
	    					   border : false,		
	    					   readOnly : true,
	    					   readOnlyCls : 'win-textfield-readOnly',
	    					   fieldStyle : 'background:#d6d6d6'
	    				   },{
	    					   xtype : 'datefield',
	    					   name : "prj_start",
	    					   id:'start',
	    					   fieldLabel : '计划开始日期',
	    					   border : false
	    				   },{
	    					   xtype : 'datefield',
	    					   name : "prj_end",
	    					   id:'end',
	    					   fieldLabel : '计划结束日期',
	    					   border : false
	    				   },{
	    					   xtype : 'combo',
	    					   name:'prj_class',
	    					   fieldLabel : '项目等级',
	    					   border : false,
	    					   store: {
	    						   fields: ['display','value'],
	    						   data : [					    	
	    						           {display:'高', value:'高'},
	    						           {display:'中', value:'中'},
	    						           {display:'低', value:'低'}
	    						           ]
	    					   },
	    					   queryMode: 'local', 
	    					   displayField: 'display',
	    					   valueField: 'value'
	    				   },{
	    					   xtype:'textarea',
	    					   columnWidth:1,
	    					   fieldLabel:'批注',
	    					   name:'wrd_auditdetail'
	    				   },{
	    					   xtype:'hidden',
	    					   name:'prj_id'
	    				   },{
	    					   xtype:'hidden',
	    					   name:'wrd_id'
	    				   }],
	    				   buttons:[{   xtype : 'erpUpdateButton',
	    					   id : 'updateButton',
	    					   width : 70,
	    					   handler : function(btn) {
	    						   var form = Ext.getCmp('updateWin');
	    						   var bool = me.checkFormDirty(form);
	    						   var formStore = form.getForm().getValues();
	    						   var bool = me.checkTime(formStore.prj_start,formStore.prj_end);
	    						   if('small'==bool){
	    							   Ext.Msg.alert('提示','计划开始日期不能小于当前日期');
	    						   }else if('over'==bool){
	    							   Ext.Msg.alert('提示','计划开始日期不能大于计划结束日期');
	    						   }else{
	    							   Ext.Ajax.request({
	    								   url:basePath + 'plm/task/updateProject.action',
	    								   method:'post',
	    								   params:{
	    									   formStore:Ext.encode(formStore)
	    								   },
	    								   callback:function(options,success,response){
	    									   if(success){
	    										   var res = Ext.decode(response.responseText);
	    										   if(res.success){
	    											   showMessage('提示','更新成功',1000);
	    											   //重新加载数据
	    											   Ext.getCmp('dealform').onQuery();
	    											   btn.ownerCt.ownerCt.ownerCt.close();
	    										   }
	    									   }
	    								   }
	    							   });
	    						   }
	    					   }
	    				   },{
	    					   xtype : 'erpCloseButton',
	    					   id : 'closeButton',
	    					   width : 70,
	    					   handler : function(btn) {
	    						   this.ownerCt.ownerCt.ownerCt.close();
	    					   }
	    				   }],
	    				   buttonAlign:'center'
	    			   }]

	    		   });
	    		   win.show();
	    	   }
	    	   Ext.getCmp('updateWin').getForm().setValues(record.data);
	       },
	       checkFormDirty:function(form){
	    	   Ext.Array.each(form.getForm().getValues(),function(item,index){
	    		   var value = item.value==null?'':item.value;
	    		   item.originalValue = item.originalValue==null?'':item.originalValue;
	    		   if(Ext.typeOf(item.originalValue)!='object'){
	    			   if(item.originalValue!=item.value){
	    				   return true;
	    			   }
	    		   }
	    	   });
	    	   return false;
	       },
	       checkTime: function(start,end){
	    	   var extCurrentDate = Ext.Date.format(new Date,'Y-m-d');
	    	   var startdate = new Date(start);
	    	   var enddate = new Date(end);
	    	   var extCurrentDate = new Date(extCurrentDate); 
	    	   if(startdate>enddate){
	    		   return 'over';
	    	   }else{
	    		   return true;
	    	   }   		
	       },
	       setComboTime:function(){
	    	   var from = Ext.getCmp('wr_date_from');
	    	   var to = Ext.getCmp('wr_date_to');

	    	   var Nowdate=new Date();
	    	   var WeekFirstDay=new Date(Nowdate-(Nowdate.getDay()-1)*86400000); //本周第一天
	    	   var WeekLastDay=new Date((WeekFirstDay/1000+6*86400)*1000);	//本周最后一天

	    	   from.setMaxValue(WeekLastDay);
	    	   from.setMinValue(WeekFirstDay);
	    	   to.setMaxValue(WeekLastDay);
	    	   to.setMinValue(WeekFirstDay);

	    	   from.setValue(WeekFirstDay);
	    	   to.setValue(WeekLastDay);
	       }
});