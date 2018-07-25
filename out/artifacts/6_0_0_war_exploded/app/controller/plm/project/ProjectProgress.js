Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectProgress', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.project.ProjectProgress','plm.project.ProgressGrid','core.form.ConDateField','core.form.MultiField','core.trigger.DbfindTrigger'
	       ],
	       init:function(){
	    	   var me=this;
	    	   this.control({ 
	    		   'button[id=query]': {
	    			   afterrender: function(btn) {
	    				   setTimeout(function(){
	    					   me.showFilterPanel(btn);
	    				   }, 200);
	    			   },
	    			   click: function(btn) {
	    				   me.showFilterPanel(btn);
	    			   }
	    		   }
	    	   });
	       },
	       showFilterPanel: function(btn) {
	    	   var filter = Ext.getCmp(btn.getId() + '-filter');
	    	   if(!filter) {
	    		   filter = this.createFilterPanel(btn);
	    	   }
	    	   filter.show();
	       },
	       hideFilterPanel: function(btn) {
	    	   var filter = Ext.getCmp(btn.getId() + '-filter');
	    	   if(filter) {
	    		   filter.hide();
	    	   }
	       },
	       createFilterPanel: function(btn) {
	    	   var me = this;
	    	   var filter = Ext.create('Ext.Window', {
	    		   id: btn.getId() + '-filter',
	    		   title: '筛选条件',
	    		   width: 510,
	    		   height: 415,
	    		   modal:true,
	    		   layout:'fit',
	    		   items:[{
	    			   xtype:'form',
	    			   id:'filterForm',
	    			   layout: 'column',
	    			   frame:true,
	    			   defaults: {
	    				   margin: '2 2 2 10'
	    			   },
	    			   items: [{
	    				   xtype:'condatefield',
	    				   id:'prj_start',
	    				   name:'prj_start',
	    				   fieldLabel:'项目时间',
	    				   labelWidth:70,
	    				   columnWidth:1,
	    				   value:7
	    			   },{
	    				   xtype:'combo',
	    				   name:'prj_status',
	    				   fieldLabel:'项目状态',
	    				   labelWidth:70,
	    				   columnWidth:0.5,
	    				   queryMode: 'local',
	    				   displayField: 'prj_status',
	    				   valueField: 'prj_status',
	    				   store:Ext.create('Ext.data.Store', {
	    					   fields: ['prj_status'],
	    					   data:[{prj_status:'未启动'},
	    					         {prj_status:'暂停中'},
	    					         {prj_status:'已启动'},
	    					         {prj_status:'已结案'},{prj_status:'全部'}]
	    				   }),
	    				   value:'已启动'
	    			   },{
	    				   xtype:'checkbox',
	    				   boxLabel:'只筛选超期项目',
	    				   columnWidth:1,
	    				   name:'overtime',
	    				   afterBoxLabelTextTpl:'<a href="#"><span class="help-terms x-button-icon-help">&nbsp;</span></a>',
	    				   tipTpl: Ext.create('Ext.XTemplate', '<ul><tpl for="."><li><span class="field-name">{name}</span>: <span class="error">{error}</span></li></tpl></ul>'),
	    				   listeners: {
	    					   click: {
	    						   element: 'boxLabelEl',
	    						   fn: function(e,el) {
	    							   var target = e.getTarget('.help-terms');
	    							   var tip = this.tip;
	    							   if (!tip) {
	    								   tip = this.tip = Ext.widget('tooltip', {
	    									   target: this.el,
	    									   title: '超期项目:',
	    									   autoHide: true,
	    									   anchor: 'left',
	    									   mouseOffset: [15, -2],
	    									   closable: true,
	    									   constrainPosition: false,
	    									   cls: 'errors-tip',
	    									   html:'项目阶段已超出原计划结束日期!'
	    								   });	    				
	    								  
	    							   }
	    							   tip.show();
	    						   }
	    					   }
	    				   }
	    			   }]
	    		   }],
	    		   buttonAlign: 'center',
	    		   buttons: [{
	    			   text: '确定',
	    			   handler: function(btn) {
	    				   var fl = btn.ownerCt.ownerCt.down('form'),
	    				   con = me.getCondition(fl);
	    				   me.query(con);
	    				   fl.ownerCt.hide();
	    			   }
	    		   },{
	    			   text: '关闭',
	    			   handler: function(btn) {
	    				   var fl = btn.ownerCt.ownerCt;
	    				   fl.hide();
	    			   }
	    		   }]
	    	   });
	    	   
	    	   //取得项目申请单的立项类型下拉框
	    	   Ext.Ajax.request({
	    	       url:basePath + 'common/getFieldsDatas.action',
	    	       method:'post',
	    	       params:{
	    	       	   caller:'datalistcombo',
	    	       	   fields:'dlc_value,dlc_display',
	    	       	   condition:"dlc_caller='ProjectRequest' and dlc_fieldname='prj_type' order by dlc_detno"
	    	       },
	    	       callback:function(options,success,response){
	    	       	   var res = Ext.decode(response.responseText);
	    	       	   if(res.data&&'[]'!=res.data){
	    	       	   	   var redata = res.data.replace(/DLC_DISPLAY/g,'value').replace(/DLC_VALUE/g,'display');
	    	       	   	   var data = Ext.decode(redata);
	    	       	   	   data.push({display:'全部',value:'全部'});
						   if(data.length>0){
						   	   var combo = {
									xtype:'combo',
									fieldLabel: "立项类型", 
									columnWidth:0.5,
									labelWidth:70,
									id:'prj_type',
									name:'prj_type',
									displayField:'display',
									valueField:'value',
									editable: false, 
									store: Ext.create('Ext.data.Store', {
										fields: ['display', 'value'],
										data:data
									}),
									value:'全部'
						   	   };
						   	   
						   	   var form = Ext.getCmp('filterForm');
						   	   form.insert(2,combo);
						   }
	    	       	   }
	    	       }
	    	   });
	    	   
	    	   return filter;
	       },
	       getCondition:function(pl){
	    	   var condition="";
	    		Ext.each(pl.items.items, function(f){
	    			if(!Ext.isEmpty(f.value)){
					  if(contains(f.value.toString(), 'BETWEEN', true) && contains(f.value.toString(), 'AND', true)){
							if(condition == ''){
								condition += f.name + " " + f.value;
							} else {
								condition += ' AND (' + f.name + " " + f.value + ")";
							}
						}
					   if(f.name=='overtime' && f.value){
						   if(condition == ''){
							   condition+="(exists (select  1 from projectphase where PP_PHASE=PRJ_PHASE and nvl(pp_status,' ')<>'已完成' and pp_prjid=prj_id and PP_ENDDATE+1<sysdate))";//计划完成日期只记录到日，没有记录时分秒，需要加1天
						   }else {
							   condition+=" AND (exists (select  1 from projectphase where PP_PHASE=PRJ_PHASE and nvl(pp_status,' ')<>'已完成' and pp_prjid=prj_id and PP_ENDDATE+1<sysdate))"	;						   
						   }
					   }
					   if(f.name=='prj_status' &&  f.value!='全部'){
						   if(condition == ''){
							   condition+="(prj_status='"+f.value+"')";
						   }else {
							   condition+=" AND (prj_status='"+f.value+"')"	;			   
						   }
					   }
					   if(f.name=='prj_type' &&  f.value!='全部'){
						   if(condition == ''){
							   condition+="(prj_type='"+f.value+"')";
						   }else {
							   condition+=" AND (prj_type='"+f.value+"')"	;			   
						   }
					   }
					}
	    		});
			return condition;
	       },
	       query:function(condition){
	    	   var g=Ext.getCmp('progressgrid');
	    	   if(g) g.loadNewStore(g,condition);
	       }
});