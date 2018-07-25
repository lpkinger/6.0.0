Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SaleForecast', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','scm.sale.SaleForecast','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.button.UpdateForecastQty',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ChangeDate','core.form.MultiField',
	       'core.button.ResAudit','core.button.Scan','core.button.DeleteDetail','core.button.ResSubmit','core.button.FeatureDefinition',
	       'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.End','core.button.ResEnd','core.button.Print',
	       'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.FeatureView','core.button.AttendDataCom',
	       'core.button.MrpOpen','core.button.MrpClose','core.button.SaleForecastChange'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'erpGridPanel2': { 
	    			   itemclick: function(selModel, record){
	    				   //Ext.getCmp('featuredefinition').setDisabled(false);
	    				   //Ext.getCmp('featureview').setDisabled(false); 
	    				   var btn = Ext.getCmp('MrpOpen');
	    				   btn && btn.setDisabled(false); 
	    				   btn = Ext.getCmp('MrpClose');
	    				   btn && btn.setDisabled(false); 
	    				   btn = Ext.getCmp('bomopen');
	    				   btn && btn.setDisabled(false);
	    				   btn = Ext.getCmp('splitSaleButton');
						   btn && btn.setDisabled(false);
	    				   this.onGridItemClick(selModel, record);
	    			   }
	    		   },
	    		   'erpChangeDateButton':{
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('sf_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var keyvalue=Ext.getCmp('sf_id').value;
	    				   var condition='sd_sfid='+keyvalue;
	    				   var win = new Ext.window.Window({
	    					   id : 'win',
	    					   height: "100%",
	    					   width: "80%",
	    					   maximizable : true,
	    					   buttonAlign : 'center',
	    					   layout : 'anchor',
	    					   items: [{
	    						   tag : 'iframe',
	    						   frame : true,
	    						   anchor : '100% 100%',
	    						   layout : 'fit',
	    						   html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=SaleForecast!Change' 
	    						   +"&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	    					   }],
	    					   buttons : [{
	    						   text : $I18N.common.button.erpConfirmButton,
	    						   iconCls: 'x-button-icon-confirm',
	    						   cls: 'x-btn-gray',
	    						   handler : function(){
	    							   var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
	    							   var data = grid.getEffectData();                      
	    							   if(data != null){
	    								   grid.setLoading(true);
	    								   Ext.Ajax.request({
	    									   url : basePath + 'scm/sale/SaleForecastChangedate.action',
	    									   params: {
	    										   caller: caller,
	    										   data: Ext.encode(data)
	    									   },
	    									   method : 'post',
	    									   callback : function(options,success,response){
	    										   grid.setLoading(false);
	    										   var localJson = new Ext.decode(response.responseText);
	    										   if(localJson.exceptionInfo){
	    											   showError(localJson.exceptionInfo);
	    											   return "";
	    										   }
	    										   if(localJson.success){
	    											   if(localJson.log){
	    												   showMessage("提示", localJson.log);
	    											   }
	    											   Ext.Msg.alert("提示", "处理成功!", function(){
	    												   win.close();
	    												   var detailgrid= Ext.getCmp('grid');		   					   
	    												   gridParam = {caller: 'SaleForecast', condition: condition};
	    												   me.GridUtil.getGridColumnsAndStore(detailgrid, 'common/singleGridPanel.action', gridParam, "");
	    											   });
	    										   }
	    									   }
	    								   });
	    							   }
	    						   }
	    					   }, {
	    						   text : $I18N.common.button.erpCloseButton,
	    						   iconCls: 'x-button-icon-close',
	    						   cls: 'x-btn-gray',
	    						   handler : function(){
	    							   Ext.getCmp('win').close();
	    						   }
	    					   }]
	    				   });
	    				   win.show();

	    			   }			
	    		   },
	    		   /**
		       		 * 订单分拆
		       		 */
		       		'#splitSaleButton': {
		       			click: function(btn) {
		       				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
		       			    me.SaleSplit(record);
		       			}
		       		},
	    		   'erpSaleForecastChangeButton':{
	    			   afterrender:function(btn){
	    				  /* btn.setDisabled(false);*/
	    				   var status = Ext.getCmp('sf_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   
	    			   }  
	    		   },
	    		   'field[name=sf_currency]': {
		    			beforetrigger: function(field) {
		    				var t = field.up('form').down('field[name=sf_tilldate]'),
		    					value = t.getValue();
		    				if(value) {
		    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
		    				}
		    			}
    				},
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
	    				   if(codeField.value == null || codeField.value == ''){
	    					   me.BaseUtil.getRandomNumber(caller);//自动添加编号
	    					   var res = me.getLeadCode(Ext.getCmp('sf_kind').value);
	    					   if(res != null && res != ''){
	    						   codeField.setValue(res + codeField.getValue());
	    					   }
	    				   }
	    				   this.beforeSaveSaleForecast(this);
	    			   }
	    		   },
	    		  /* 'erpDeleteDetailButton': {
	    			   afterrender: function(btn){
	    				   btn.ownerCt.add({
	    					   xtype: 'erpFeatureDefinitionButton'
	    				   });
	    				   btn.ownerCt.add({
	    					   xtype: 'erpFeatureViewButton'
	    				   }); 
	    			   }
	    		   },*/
	    		   'erpDeleteButton' : {
	    			   click: function(btn){
	    				   me.FormUtil.onDelete(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    				   this.beforeUpdate(this);
	    			   }
	    		   },
	    		   'erpAttendDataComButton':{
	    			   beforerender:function(btn){
	    				   btn.setText("计  算");
	    				   btn.setWidth(65);
	    			   },
	    			   click: function(btn){
	    				   grid = Ext.getCmp('grid');
	    				   var id=Ext.getCmp('sf_id').value;
	    				   Ext.Ajax.request({
	    					   url : basePath + "scm/sale/saleforecastdataupdate.action",
	    					   params: {
	    						   id:id
	    					   },
	    					   method : 'post',
	    					   async: false,
	    					   callback : function(options,success,response){
	    						   var res = new Ext.decode(response.responseText);
	    						   if(res.exceptionInfo){
	    							   showError(res.exceptionInfo);
	    							   return;
	    						   }
	    						   grid.GridUtil.loadNewStore(grid,{
	    							   caller:'SaleForecast',
	    							   condition:gridCondition,
	    							   _noc:1
	    						   });
	    						   showError("计算成功！");							
	    					   }
	    				   });
	    			   }
	    		   },
	    		   'erpMrpOpenButton':{
	    			   click: function(btn){
	    				   var grid = Ext.getCmp('grid');
	    				   var record = grid.selModel.lastSelected;
	    				   var id = record.data.sd_id;
	    				   Ext.Ajax.request({
	    					   url : basePath + "scm/sale/openMrp.action",
	    					   params: {
	    						   id:id
	    					   },
	    					   method : 'post',
	    					   async: false,
	    					   callback : function(options,success,response){
	    						   var res = new Ext.decode(response.responseText);
	    						   if(res.exceptionInfo){
	    							   showError(res.exceptionInfo);
	    							   return;
	    						   }
	    						   showError("打开Mrp成功！");
	    					   }
	    				   });
	    			   }
	    		   },
	    		   'erpMrpCloseButton': {
	    			   click: function(btn){
	    				   var grid = Ext.getCmp('grid');
	    				   var record = grid.selModel.lastSelected;
	    				   var id = record.data.sd_id;
	    				   Ext.Ajax.request({
	    					   url : basePath + "scm/sale/CloseMrp.action",
	    					   params: {
	    						   id:id
	    					   },
	    					   method : 'post',
	    					   async: false,
	    					   callback : function(options,success,response){
	    						   var res = new Ext.decode(response.responseText);
	    						   if(res.exceptionInfo){
	    							   showError(res.exceptionInfo);
	    							   return;
	    						   }
	    						   showError("关闭Mrp成功！");
	    					   }
	    				   });
	    			   }
	    		   },
	    		   /**
	    		    * BOM多级展开 
	    		    */
	    		   '#bomopen': {
	    			   click: function(btn) {
	    				   var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    
	    				   var pr_code=record.data.sd_prodcode;
	    				   var url="jsps/pm/bom/BOMStructQuery.jsp?whoami=BOMStruct!Struct!Query";
	    				   var condition="";
	    				   //母件编号带出展开的料号不对  参照万利达配置
	    				   if(pr_code){
	    					   condition+="pr_codeIS'"+pr_code+"'";
	    				   }
	    				   me.FormUtil.onAdd('BOMStruct'+ pr_code, 'BOM多级展开', url+"&condition="+condition);
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addSaleForecast', '新增销售预测单', 'jsps/scm/sale/saleForecast.jsp');
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('sf_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    			   	   var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool=true;
				    	   	Ext.Array.each(items, function(item){
				    	   		if(!Ext.isEmpty(item.data['sd_prodcode'])){
				    	   			if(!Ext.isEmpty(item.data['sd_needdate'])){
						    		   if (Ext.Date.format(item.data['sd_needdate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
						                    bool = false;
						                    showError('明细表第' + item.data['sd_detno'] + '行的出货日期小于当前日期');
						                    return;
						               }
				    	   			}
					    		   if(!Ext.isEmpty(item.data['sd_enddate'])){
					    			   if (Ext.Date.format(item.data['sd_enddate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
						                    bool = false;
						                    showError('明细表第' + item.data['sd_detno'] + '行的截止日期小于当前日期');
						                    return;
						               }
						    		   if (item.data['sd_enddate'] < item.data['sd_startdate']) {
						                    bool = false;
						                    showError('明细表第' + item.data['sd_detno'] + '行的截止日期小于起始日期');
						                    return;
						               }
					    		   }
				    	   		}
				    	   });
				    	   if(bool){
				    	   		me.FormUtil.onSubmit(Ext.getCmp('sf_id').value);
				    	   }
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('sf_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('sf_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('sf_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpPrintButton': {
	    			   click:function(btn){
	    				   var reportName="SaleForecastAudit1";
	    				   var condition='{SaleForeCast.sf_id}='+Ext.getCmp('sf_id').value+'';
	    				   var id=Ext.getCmp('sf_id').value;
	    				   me.FormUtil.onwindowsPrint(id,reportName,condition);
	    			   }
	    		   },
	    		   'erpEndButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('sf_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onEnd(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpResEndButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('sf_statuscode');
	    				   if(status && status.value != 'FINISH'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResEnd(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpFeatureDefinitionButton':{
	    			   click: function(btn){
	    				   var grid = Ext.getCmp('grid');
	    				   var record = grid.selModel.lastSelected;
	    				   if(record.data.sd_prodcode != null){
	    					   Ext.Ajax.request({//拿到grid的columns
	    						   url : basePath + "pm/bom/getDescription.action",
	    						   params: {
	    							   tablename: 'Product',
	    							   field: 'pr_specvalue',
	    							   condition: "pr_code='" + record.data.sd_prodcode + "'"
	    						   },
	    						   method : 'post',
	    						   async: false,
	    						   callback : function(options,success,response){
	    							   var res = new Ext.decode(response.responseText);
	    							   if(res.exceptionInfo){
	    								   showError(res.exceptionInfo);return;
	    							   }
	    							   if(res.success){
	    								   if(res.description != '' && res.description != null && res.description == 'NOTSPECIFIC'){
	    									   var win = new Ext.window.Window({
	    										   id : 'win',
	    										   title: '生成特征料号',
	    										   height: "90%",
	    										   width: "70%",
	    										   maximizable : true,
	    										   buttonAlign : 'center',
	    										   layout : 'anchor',
	    										   items: [{
	    											   tag : 'iframe',
	    											   frame : true,
	    											   anchor : '100% 100%',
	    											   layout : 'fit',
	    											   html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
	    											   "jsps/pm/bom/FeatureValueSet.jsp?fromwhere=SaleForecastDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
	    										   }]
	    									   });
	    									   win.show();   									
	    								   } else {
	    									   showError('物料特征必须为 虚拟特征件');return;
	    								   }
	    							   }
	    						   }
	    					   });
	    				   }
	    			   }
	    		   },
	    		   'erpFeatureViewButton':{
	    			   click: function(btn){
	    				   var grid = Ext.getCmp('grid');
	    				   var record = grid.selModel.lastSelected;
	    				   if(record.data.sd_prodcode != null){
	    					   Ext.Ajax.request({//拿到grid的columns
	    						   url : basePath + "pm/bom/getDescription.action",
	    						   params: {
	    							   tablename: 'Product',
	    							   field: 'pr_specvalue',
	    							   condition: "pr_code='" + record.data.sd_prodcode + "'"
	    						   },
	    						   method : 'post',
	    						   async: false,
	    						   callback : function(options,success,response){
	    							   var res = new Ext.decode(response.responseText);
	    							   if(res.exceptionInfo){
	    								   showError(res.exceptionInfo);return;
	    							   }
	    							   if(res.success){
	    								   if(res.description != '' && res.description != null && res.description == 'SPECIFIC'){
	    									   var win = new Ext.window.Window({
	    										   id : 'win' + record.data.sd_id,
	    										   title: '特征查看',
	    										   height: "90%",
	    										   width: "70%",
	    										   maximizable : true,
	    										   buttonAlign : 'center',
	    										   layout : 'anchor',
	    										   items: [{
	    											   tag : 'iframe',
	    											   frame : true,
	    											   anchor : '100% 100%',
	    											   layout : 'fit',
	    											   html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
	    											   "jsps/pm/bom/FeatureValueView.jsp?fromwhere=SaleForecastDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
	    										   }]
	    									   });
	    									   win.show();    									
	    								   } else {
	    									   showError('物料特征必须为 虚拟特征件');return;
	    								   }
	    							   }
	    						   }
	    					   });
	    				   }
	    			   }
	    		   },
	    		   'erpUpdateForecastQtyButton':{
	    			   click:function(){
	    				   //更改预测数量	
	    				   var id=Ext.getCmp('sf_id').value;
	    				   var condition="sd_sfid="+id;
	    				   var linkCaller='SaleForecast!UpdateQty';
	    				   var win = new Ext.window.Window(
	    						   {  
	    							   id : 'win',
	    							   height : 300,
	    							   width : 500,
	    							   maximizable : true,
	    							   title:'更改预测数量',
	    							   buttonAlign : 'center',
	    							   layout : 'anchor',
	    							   items : [ {
	    								   tag : 'iframe',
	    								   frame : true,
	    								   anchor : '100% 100%',
	    								   layout : 'fit',
	    								   html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/common/editorColumn.jsp?_noc=1&caller='+linkCaller+'&condition='+condition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
	    							   } ],
	    							   buttons:[{
	    								   text:'保存',
	    								   iconCls: 'x-button-icon-save',
	    								   cls: 'x-btn-gray',
	    								   handler:function(btn){
	    									   var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
	    									   var data = grid.getEffectData();                      
	    									   if(data != null){
	    										   grid.setLoading(true);
	    										   Ext.Ajax.request({
	    											   url : basePath + 'scm/sale/UpdateForecastQty.action',
	    											   params: {
	    												   data: Ext.encode(data)
	    											   },
	    											   method : 'post',
	    											   callback : function(options,success,response){
	    												   grid.setLoading(false);
	    												   var localJson = new Ext.decode(response.responseText);
	    												   if(localJson.exceptionInfo){
	    													   showError(localJson.exceptionInfo);
	    													   return "";
	    												   }
	    												   if(localJson.success){
	    													   if(localJson.log){
	    														   showMessage("提示", localJson.log);
	    													   }
	    													   Ext.Msg.alert("提示", "处理成功!", function(){
	    														   win.close();
	    														   var grid=Ext.getCmp('grid');
	    														   grid.GridUtil.loadNewStore(grid,{
	    															   caller:'SaleForecast',
	    															   condition:gridCondition,
	    															   _noc:1
	    														   });
	    													   });
	    												   }
	    											   }
	    										   });
	    									   }
	    								   }
	    							   },{
	    								   text:'关闭',
	    								   iconCls: 'x-button-icon-close',
	    								   cls: 'x-btn-gray',
	    								   handler:function(btn){
	    									   win.close();
	    								   }

	    							   }]

	    						   });
	    				   win.show();  	
	    			   } 			
	    		   },
	    		   'textfield[name=sf_fromdate]': {
	    			   change: function(field){
	    				   if(field.value != null && field.value != ''){
	    					   var grid = Ext.getCmp('grid');
	    					   var date = field.value;
	    					   Ext.Array.each(grid.getStore().data.items,function(item){
	    						   item.set('sd_startdate',date);
	    					   });
	    				   }
	    			   }
	    		   },
	    		   'dbfindtrigger[name=sd_custprodcode]': {
	      			   focus: function(t){
	      				   t.setHideTrigger(false);
	      				   t.setReadOnly(false);
	      				   if(Ext.getCmp('sf_custcode')){
	      					   var cucode = Ext.getCmp('sf_custcode').value,
	      					   	   record = Ext.getCmp('grid').selModel.lastSelected;
	      					   if(Ext.isEmpty(cucode)){
	     	    					 showError("请先选择客户编号!");
	     	    					 t.setHideTrigger(true);
	     	    					 t.setReadOnly(true);
	     	    			   } else {
	     	    				   t.dbBaseCondition = "pc_custcode='" + cucode + "'";
	      					   }
	      				   }
	      			   	}
	      		    },
	    		   'textfield[name=sf_todate]': {
	    			   change: function(field){
	    				   if(field.value != null && field.value != ''){
	    					   var grid = Ext.getCmp('grid');
	    					   var date = field.value;
	    					   Ext.Array.each(grid.getStore().data.items,function(item){
	    						   item.set('sd_enddate',date);
	    					   });
	    				   }
	    			   }
	    		   },
	    		   'textfield[name=sf_custcode]': {//主表客户编号改变时不对明细行客户编号赋值
	    		   	/* change: function(field){
	    				   if(field.value != null && field.value != ''){
	    					   var grid = Ext.getCmp('grid');
	    					   var date = field.value;
	    					   var name = Ext.getCmp('sf_custname').value;
	    					   Ext.Array.each(grid.getStore().data.items,function(item){
	    						   item.set('sd_custcode',date);
	    						   item.set('sd_custname',name);
	    					   });
	    				   }
	    			   }
	    		   */}
	    	   });
	       }, 
	       onGridItemClick: function(selModel, record){//grid行选择
	    	   this.GridUtil.onGridItemClick(selModel, record);
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       beforeSaveSaleForecast: function(){
	    	   var grid = Ext.getCmp('grid'), items = grid.store.data.items, 
	    	   	   c = Ext.getCmp('sf_code').value, bool = true;
	    	   Ext.Array.each(items, function(item){
	    		   if(!Ext.isEmpty(item.data['sd_prodcode'])){
	    			   		item.set('sd_code', c);
		    		   		item.set('sd_sourceqty', Number(item.data['sd_qty']));
		    		   		if(!Ext.isEmpty(item.data['sd_needdate'])){
			    		   		if (Ext.Date.format(item.data['sd_needdate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['sd_detno'] + '行的出货日期小于当前日期');
				                    return;
			    		   		}
		    		   		}
				    		if(!Ext.isEmpty(item.data['sd_enddate'])){
				    			if (Ext.Date.format(item.data['sd_enddate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
					                    bool = false;
					                    showError('明细表第' + item.data['sd_detno'] + '行的截止日期小于当前日期');
					                    return;
				    			}
				    			if (item.data['sd_enddate'] < item.data['sd_startdate']) {
				    				bool = false;
					                showError('明细表第' + item.data['sd_detno'] + '行的截止日期小于起始日期');
					                return;
					            }
				    		}
	    		   	 }
	    	   });
	    	   if(bool)
		    	   //保存
		    	   this.FormUtil.beforeSave(this);
	       },
	       beforeUpdate: function(){
	    	   	var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('sf_code').value, bool = true;;
	    	   	Ext.Array.each(items, function(item){
	    	   		if(!Ext.isEmpty(item.data['sd_prodcode'])){
			    		   item.set('sd_code', c);
			    		   item.set('sd_sourceqty', Number(item.data['sd_qty']));
			    		   if(!Ext.isEmpty(item.data['sd_needdate'])){
			    		   		if (Ext.Date.format(item.data['sd_needdate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['sd_detno'] + '行的出货日期小于当前日期');
				                    return;
			    		   		}
		    		   	   }
			    		   if(!Ext.isEmpty(item.data['sd_enddate'])){
			    			   if (Ext.Date.format(item.data['sd_enddate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['sd_detno'] + '行的截止日期小于当前日期');
				                    return;
				               } else if (item.data['sd_enddate'] < item.data['sd_startdate']) {
				                    bool = false;
				                    showError('明细表第' + item.data['sd_detno'] + '行的截止日期小于起始日期');
				                    return;
				               }
			    		   }
	    	   		}
	    	   });
	    	   if(bool)
		    	   //更新
		    	   this.FormUtil.onUpdate(this);	
	       },
	       getLeadCode: function(type) {
	    	   var result = null;
	    	   Ext.Ajax.request({
	    		   url : basePath + 'common/getFieldData.action',
	    		   async: false,
	    		   params: {
	    			   caller: 'SaleForecastKind',
	    			   field: 'sf_excode',
	    			   condition: 'sf_name=\'' + type + '\''
	    		   },
	    		   method : 'post',
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
	    			   if(r.exceptionInfo){
	    				   showError(r.exceptionInfo);return;
	    			   } else if(r.success){
	    				   result = r.data;
	    			   }
	    		   }
	    	   });
	    	   return result;
	       },
	    /**
	   	 *销售预测单拆分
	   	 * */
	   	SaleSplit:function(record){
	   		var me=this,originaldetno=Number(record.data.sd_detno);
	   		var sfid=record.data.sd_sfid;
	   		var sdid=record.data.sd_id;
	   		Ext.create('Ext.window.Window',{
	       		width:850,
	       		height:'80%',
	       		iconCls:'x-grid-icon-partition',
	       		title:'<h1>销售预测单拆分</h1>',
	       		id:'win',
	       		items:[{
	       			xtype:'form',
	       			layout:'column',
	       			region:'north',
	       			frame:true,
	       			defaults:{
	       				xtype:'textfield',
	       				columnWidth:0.5,
	       				readOnly:true,
	       				fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;'
	       			},
	       			items:[{
	       			 fieldLabel:'行号',
	       			 value:record.data.sd_detno,
	       			 id:'sfdetno'
	       			},{
	       			 fieldLabel:'产品编号'	,
	       			 value:record.data.sd_prodcode
	       			},{
	       			 fieldLabel:'产品名称',
	       			 value:record.data.pr_detail
	       			},{
	      			  fieldLabel:'公司型号',
	    			  value:record.data.sd_companytype
	    			},{
	       			 fieldLabel:'原序号'	,
	       			 value:record.data.sd_detno
	       			},{
	       		     fieldLabel:'原数量',
	       		     value:record.data.sd_qty,
	       		     id:'sdqty'
	       			}],
	       			buttonAlign:'center',
	       			buttons:[{
	       				xtype:'button',
	       				columnWidth:0.12,
	       				text:'保存',
	       				width:60,
	       				iconCls: 'x-button-icon-save',
	       				margin:'0 0 0 30',
	       				handler:function(btn){
	       				   var store=Ext.getCmp('smallgrid').getStore();
	       				   var count=0;
	       				   var jsonData=new Array();
	       				   var dd; 
	       				   var remainqty;
	       				   Ext.Array.each(store.data.items,function(item){
	       					  if(item.data.sd_qty!=0&&item.data.sd_needdate!=null&&item.data.sd_qty>0){
	       						  if(item.dirty){
	       							  dd=new Object();
	       							  //说明是新增批次
	       							  if(item.data.sd_needdate)
	       								  dd['sd_needdate']=Ext.Date.format(item.data.sd_needdate, 'Y-m-d');
	       							  if(item.data.sd_enddate)
	       								  dd['sd_enddate']=Ext.Date.format(item.data.sd_enddate, 'Y-m-d');
	       							  dd['sd_qty']=item.data.sd_qty; 
	       							  dd['sd_id']=item.data.sd_id;
	       							  dd['sd_detno']=item.data.sd_detno;
	       							  jsonData.push(Ext.JSON.encode(dd));
	       							  if(item.data.sd_enddate){
	       								  if(Ext.Date.format(item.data.sd_enddate, 'Y-m-d') <Ext.Date.format(new Date(), 'Y-m-d') ){
	         		    				   		showError('截止日期必须大于等于系统当前日期!') ;  
	         			    					return;
	       								  }
	       							  } 
	       							  if(item.data.sd_id!=0&&item.data.sd_id!=null&&item.data.sd_id>0){
	       								  remainqty=item.data.sd_qty; 
	     								  }
	       						  }
	       						  count+=Number(item.data.sd_qty);
	       					  } 
	       				   });	  
	       				   var assqty=Ext.getCmp('sdqty').value;
	       				   if(count!=assqty){
	   	    					showError('分拆数量必须等于原数量!') ;  
	   	    					return;
	       				   }else{
	       					   var r=new Object();
	           				   r['sd_id']=record.data.sd_id;
	           				   r['sd_sfid']=record.data.sd_sfid;
	           				   r['sd_detno']=record.data.sd_detno;      
	           				   if(record.data.sd_enddate)
	           					   r['sd_enddate']=Ext.Date.format(record.data.sd_enddate,'Y-m-d');
	           				   if(record.data.sd_needdate)
	           					   r['sd_needdate']=Ext.Date.format(record.data.sd_needdate,'Y-m-d');
	           				   var params=new Object();
	           				   params.formdata = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
	           				   params.data = unescape(jsonData.toString().replace(/\\/g,"%"));
	       					   Ext.Ajax.request({
	       					   	  url : basePath +'scm/sale/splitSaleForecast.action',
	       					   	  params : params,
	       					   	  waitMsg:'拆分中...',
	       					   	  method : 'post',
	       					   	  callback : function(options,success,response){
	       					   		var localJson = new Ext.decode(response.responseText);
	       					   		if(localJson.success){
	       			    				saveSuccess(function(){
	       			    					Ext.getCmp('sdqty').setValue(remainqty);
	       			    					//add成功后刷新页面进入可编辑的页面 
	       			    					me.loadSplitData(originaldetno,sfid,record);  
	       			    				});
	       				   			} else if(localJson.exceptionInfo){
	       				   				var str = localJson.exceptionInfo;
	       				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	       				   					str = str.replace('AFTERSUCCESS', '');
	       				   					saveSuccess(function(){
	       				    					//add成功后刷新页面进入可编辑的页面 
	       				   					 me.loadSplitData(originaldetno,sfid,record);  
	       				    				});
	       				   					showError(str);
	       				   				} else {
	       				   					showError(str);
	       					   				return;
	       				   				}
	       					   			
	       					   	 } else{
	       				   				saveFailure();
	       				   			}
	       					   	  }
	       					   });
	       					   
	       				   }
	       				}
	       			},{
	       				xtype:'button',
	       				columnWidth:0.1,
	       				text:'关闭',
	       				width:60,
	       				iconCls: 'x-button-icon-close',
	       				margin:'0 0 0 10',
	       				handler:function(btn){
	       					Ext.getCmp('win').close();
	       				}
	       			}]
	       		},{
	       		  xtype:'gridpanel',
	       		  region:'south',
	       		  id:'smallgrid',
	       		  layout:'fit',
	       		  height:'80%',
	       		  columnLines:true,
	       		  store:Ext.create('Ext.data.Store',{
	   					fields:[{name:'sd_needdate',type:'date'},{name:'sd_qty',type:'int'},{name:'sd_clashsaleqty',type:'int'},{name:'sd_yqty',type:'int'},{name:'sd_id',type:'int'},{name:'sd_enddate',type:'date'}],
	   				    data:[]
	       		  }),
	       		  plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
	       		        clicksToEdit: 1,
	       		        listeners:{
	       		        	'edit':function(editor,e,Opts){
	       		        		var record=e.record;
	       		        		var version=record.data.ma_version;
	       		        		if(version){
	       		        			e.record.reject();
	       		        			Ext.Msg.alert('提示','不能修改已拆分明细!');
	       		        		}
	       		        	}
	       		        }
	       		    })],
	       		  tbar: [{
	       			    tooltip: '添加批次',
	       	            iconCls: 'x-button-icon-add',
	       	            width:25,
	       	            handler : function() {
	       	            	var store = Ext.getCmp('smallgrid').getStore();
	       	                var r = new Object();
	       	                r.sd_needdate=record.get('sd_needdate');
	       	                r.sd_enddate=record.get('sd_enddate');
	       	                r.sd_qty=0; 
	       	                r.sd_id=0;
	       	                r.sd_detno=store.getCount()+1;
	       	                store.insert(store.getCount(), r);
	       	            }
	       	        }, {
	       	            tooltip: '删除批次',
	       	            width:25,
	       	            itemId: 'delete',
	       	            iconCls: 'x-button-icon-delete',
	       	            handler: function(btn) {
	       	                var sm = Ext.getCmp('smallgrid').getSelectionModel();
	       	                var record=sm.getSelection();
	       	                var sd_id=record[0].data.sd_id;
	       	                if(sd_id&&sd_id!=0){
	       	                	Ext.Msg.alert('提示','不能删除已拆批次或原始行号!');
	       	                	return;
	       	                }
	       	                var store=Ext.getCmp('smallgrid').getStore();
	       	                store.remove(record);
	       	                if (store.getCount() > 0) {
	       	                    sm.select(0);
	       	                }
	       	            },
	       	            disabled: true
	       	        }],
	       	      listeners:{
	       	    	  itemmousedown:function(selmodel, record){
	       	    		  selmodel.ownerCt.down('#delete').setDisabled(false);
	       	    	  },
	       	    	  afterrender : function(grid) {
	       	    		me.BaseUtil.getSetting('SaleForecast', 'sd_needdate', function(bool) {
	       					if(bool) {
	       						grid.down('gridcolumn[dataIndex=sd_needdate]').hide();
	       					}
	       	            });
	       	    		me.BaseUtil.getSetting('SaleForecast', 'sd_enddate', function(bool) {
	       					if(bool) {
	       						grid.down('gridcolumn[dataIndex=sd_enddate]').hide();
	       					}
	       	            });
	       	    	  }
	       	      }, 
	       		  columns:[{
	       			 dataIndex:'sd_detno',
	       			 header:'序号',
	       			 format:'0',
	       			 xtype:'numbercolumn'
	       		   },{
	       			  dataIndex:'sd_needdate',
	       			  header:'出货日期',
	       			  xtype:'datecolumn',
	       			  width:120,
	       			  editable:true,
	       			  renderer:function(val,meta,record){
	       				   if(record.data.ma_version){
	       					  meta.tdCls = "x-grid-cell-renderer-cl";
	       				   }
	       				   if(val)
	       					   return Ext.Date.format(val, 'Y-m-d');
	       				   else return null;
	       			   },
	       			  editor:{
	       				  xtype: 'datefield',
	       				  format:'Y-m-d'
	       			  }
	       		  },{
	       			  dataIndex:'sd_enddate',
	       			  header:'截止日期',
	       			  xtype:'datecolumn',
	       			  width:120,
	       			  editable:true,
	       			  renderer:function(val,meta,record){
	       				   if(record.data.ma_version){
	       					  meta.tdCls = "x-grid-cell-renderer-cl";
	       				   }
	       				   if(val)
	       					   return Ext.Date.format(val, 'Y-m-d');
	       				   else return null;
	       			   },
	       			  editor:{
	       				  xtype: 'datefield',
	       				  format:'Y-m-d'
	       			  }
	       		  },{
	       			  dataIndex:'sd_qty',
	       			  header:'数量',
	       			  width:120,
	       			  xtype:'numbercolumn',
	       			  editable:true,
	       			  renderer:function(val,meta,record){
	      				   if(record.data.ma_version){
	      					  meta.tdCls = "x-grid-cell-renderer-cl";
	      				   }
	      				   return val;
	      			     },
	       			  editor:{
	       				  xtype:'numberfield',
	       				  format:'0',
	       				  hideTrigger: true
	       			  }
	       		  },{
	       			dataIndex:'sd_yqty',
	       			header:'已转订单数',
	       			xtype:'numbercolumn',
	       			width:100,
	       			editable:false
	       		  },{
	       			  dataIndex:'sd_id',
	       			  header:'sdid',
	       			  width:0,
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
	        this.loadSplitData(originaldetno,sfid,record); 
	   	},
	   	loadSplitData:function(detno,sfid,record){
	   		 var grid=Ext.getCmp('smallgrid');
	            grid.setLoading(true);//loading...
	    		Ext.Ajax.request({//拿到grid的columns
	            	url : basePath + "common/loadNewGridStore.action",
	            	params:{
	            	  caller:'SaleForecastSplit',
	            	  condition:"sd_detno="+detno+" AND sd_sfid="+sfid+" order by sd_id asc"
	            	},
	            	method : 'post',
	            	callback : function(options,success,response){
	            		grid.setLoading(false);
	            		var res = new Ext.decode(response.responseText);
	            		if(res.exceptionInfo){
	            			showError(res.exceptionInfo);return;
	            		}
	            		var data = res.data;
	            		if(!data || data.length == 0){
	            			grid.store.removeAll();
	            			var o=new Object();
	            			o.sd_detno=detno;
	            			o.sd_needdate=record.data.sd_needdate;
	            			o.sd_enddate=record.data.sd_enddate;
	            			o.sd_qty=record.data.sd_qty;
	            			o.sd_yqty=record.data.sd_yqty;
	            			o.sd_clashsaleqty=record.data.sd_clashsaleqty;
	            			o.sd_id=record.data.sd_id;
	            			data.push(o);
	            		}
	            		 grid.store.loadData(data);
	            	}
	            });
	   	}
});