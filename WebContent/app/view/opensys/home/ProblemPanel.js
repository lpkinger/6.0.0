Ext.define('erp.view.opensys.home.ProblemPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.problempanel',
	cls: 'preview',
	autoScroll: true,
	region: 'south',
	border:false,
	flex: 2,
	title:'问题反馈',
    layout:'fit',
	initComponent: function(){
		Ext.apply(this, {
			items:[
				Ext.widget('panel',{
					layout: 'anchor', 
					border:true,
					items:[Ext.widget('gridpanel',{
						id:'feedbackgridpanel',
						columnLines : false,
    					autoScroll : true,
    					anchor:'100% 100%',
    					layout:'fit',
						store: Ext.create('Ext.data.Store', {
		    			    fields:['FB_ID','FB_CODE','FB_KIND','FB_THEME','FB_DETAIL','FB_POSITION','FB_DATE'], 
		       				data: []
		       				}),
					    columns:[{
									text:'标题',
									cls:'x-grid-header-simple',
									dataIndex:'FB_ID',
									resizable :false,
									//width:300,
									flex:1,
									id: 'topic',
									renderer:function(val,meta,record){
										var detail=record.get('FB_DETAIL');
										if(detail==null || detail =='' || detail=='null') {
											detail='';
										}
										else detail='</br></br><font color="#777">'+detail+'</font>';
										return Ext.String.format('<span style="color:#436EEE;padding-left:2px;"><a class="x-btn-link" onclick="openTable(\'问题反馈\',\'jsps/opensys/FeedBack.jsp?caller=Feedback!Customer&formCondition=fb_idIS'+record.get('FB_ID')+'\',null);" target="_blank"  style="padding-left:2px">{0}&nbsp;{1}</a>{2}</span>',
												record.get('FB_THEME'),
												record.get('FB_CODE'),
												detail
										);
									}
								},{
									text:'当前进展',
									cls:'x-grid-header-simple',
									width:100,
									dataIndex:'FB_POSITION',
									renderer:function(value){
										return value;
									}
								},{
									text:'发起时间',
									cls:'x-grid-header-simple',
									width:150,
									dataIndex:'FB_DATE',
									xtype:'datecolumn',
									renderer:function(value){
										return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
									}
								}]
					
					})],
					dockedItems: [this.createToolbar()]
				})]
		});
		this.loadTab();
		this.callParent(arguments);
	},
	createToolbar: function(){
		var me=this;
		var items = [],
		config = {
		 /*style:'border-left-width: 10px!important;'	*/
		};
		items.push({
			scope: this,
			handler: function(){
				me.loadTab('1=1');
			},
			text: '全部',
			iconCls: 'x-button-icon-showall'
		}, '-');
		items.push({
			scope: this,
			handler:  function(){
				var condition="fb_statuscode='FINISH'";
				this.loadTab(condition);
			},
			text: '已确认',
			iconCls: 'x-button-icon-showcomplete'
		},'-');
		items.push({
			scope: this,
			handler: function(){
				me.loadTab("fb_statuscode<>'FINISH'");
			},
			text: '未确认',
			iconCls: 'x-button-icon-showuncomplete'
		});
		config.items = items;
		return Ext.create('widget.toolbar', config);
	},
	loadTab:function(condition){
		var con='fb_enid='+enUU;
		if(condition){
			con=con+" and "+condition;
		}
		var me=this;
		var data=new Object();
		Ext.Ajax.request({
	    		   url : basePath + 'sys/feedback/getFeedback.action',
	    		    async: false,
	    		   params: {
	    			   condition:con
	    		   },
	    		   method : 'get',
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
	    			   if(r.exceptionInfo){
	    				   showError(r.exceptionInfo);return;
	    			   } else if(r.success && r.data){
	    				  Ext.getCmp('feedbackgridpanel').store.loadData(r.data);
	    			   }
	    		   }
	    });
	}
});