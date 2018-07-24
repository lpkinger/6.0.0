Ext.define('erp.view.oa.attention.AttentionFunction', {
height:0.33,
width: 0.33,
setWidth: function(width){
						this.width = width;
					},
					setHeight: function(height){
						this.height = height;
					},
  WorkDaily:function(){
	   var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
			title: '<font color=green>工作日报</font>',
		    bodyStyle: 'background: #f1f1f1',
			   //style: 'margin: 2px;',
		   id: 'bench_email1',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   eight: me.height
		  });
	},
    Agenda:function(){
        var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
			title: '<font color=green>工作日程</font>',
		    bodyStyle: 'background: #f1f1f1',
			   //style: 'margin: 2px;',
		   id: 'bench_email2',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });

    },
    WorkAttendance:function(){
         var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green>考勤</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'bench_email3',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });
    },
    JProcess2DealByMe:function(){
         var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green>待审批的流程</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'bench_email4',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });    
    },
    JProcessDeal:function(){
       var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green>发起的流程</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'bench_email5',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });
     },
   ProjectPlan:function(){
        var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green>未完成的项目</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'bench_email6',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });
   },
   WorkRecord:function(){
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green>未完成的项目</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'WorkRecord',
		   contentEl: 'mytask',
		   iconCls: 'main-news',
		   columnWidth: 0.33, 
		   height: 0.33
		  });
   },
   newSynergy:function(){
        var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green> 内部协同</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'bench_email8',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });  
   },
   ProjectFeePlease:function(){
        var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green> 内部协同</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'bench_email9',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });
   },
   ProjectFeeClaim:function(){
       var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green> 内部协同</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'bench_email99',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });
   },
   Meeting:function(){
        var me = this.workbench || this;
		  return Ext.create('Ext.panel.Panel', {
		   title: '<font color=green> 内部协同</font>',
		   bodyStyle: 'background: #f1f1f1',
		   id: 'bench_email91',
		   iconCls: 'main-news',
		   columnWidth: me.width, 
		   height: me.height
		  });
    },
   _WorkRecord: function(emid,days){
   alert('Yes');
				var me = this.workbench || this;
				      Ext.Ajax.request({
				        	url : basePath + 'common/datalist.action',
				        	params: {
				        		caller: 'ResourceAssignment',
				        		condition:  'ra_emid=' + 3014, 
				        		page: 1,
				        		pageSize: parseInt(100*0.3/12)
				        	},
				        	method : 'post',
				        	callback : function(options,success,response){
				        		var res = new Ext.decode(response.responseText);
				        		if(res.exception || res.exceptionInfo){
				        			showError(res.exceptionInfo);
				        			return;
				        		}
				        		var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
				        		if(task == [] || task.length == 0){
				        			Ext.get("mytask").insertHtml('afterBegin', '<span style="color:gray;font-size:26px; padding: 5 5 5 5;">(暂无任务)</span>');
				        		} else {
				        			Ext.create('Ext.grid.Panel', {
					        			autoScroll: true,
					        		    store: Ext.create('Ext.data.Store', {
											    fields:['ra_id', 'ra_taskname', 'ra_startdate', 'ra_enddate', 'surplus', 'ra_taskpercentdone'],
											    data: task
										}),
										height: me.height*1.8,
										bodyStyle: 'background: #f1f1f1;border: none;',
					        		    columns: [
					        		    	{ header: 'ID',  dataIndex: 'ra_id', hidden: true},
					        		        { header: '任务名称',  dataIndex: 'ra_taskname', flex: 2 },
					        		        { header: '开始时间', dataIndex: 'ra_startdate', flex: 1 },
					        		        { header: '结束时间', dataIndex: 'ra_enddate', flex: 1 },
					        		        { header: '剩余时间', dataIndex: 'surplus', flex: 1.5 },
					        		        { header: '完成率(%)', dataIndex: 'ra_taskpercentdone', flex: 1}
					        		    ],
					        		    renderTo: Ext.get("mytask")
					        		});
				        		}
				        	}
				  });
			}, 
});