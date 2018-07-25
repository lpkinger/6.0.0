Ext.define('erp.view.common.flow.FlowBottom',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.flowbottom',
	id:'historyGrid',
	region: 'south',
	hideHeaders:true,
	title: '<h1 style="color:black ! important;">&nbsp&nbsp审批历史</h1>',
	columns:[
	         {dataIndex: 'jn_dealManName',width:100,tdCls:'nodedealman'},       		              
	         {dataIndex: 'jn_dealResult',width:100,
	          renderer:function(v){
	        	if(v=='同意') {
	        		return '<span style="color:green;">'+v+'</span>';
	        	}else if(v=='不同意') {
	        		return '<span style="color:red;">'+v+'</span>';
	        	}else return v;
	          }
	         },	
	         {flex:0.85,
		      renderer:function(v,meta,record){
		        	var r=record.get('jn_dealResult'),o=record.get('jn_operatedDescription'),desc=record.get('jn_nodeDescription'),
		        	   info=record.get('jn_infoReceiver'),msg="";
	                if(r=='同意'){
	                	if(o) msg+=o;
	                	if(desc) msg=msg.length>0?msg+'<br/>'+desc:desc;
	                } else if(r=='变更处理人'){
	                	msg+=info;
	                	if(desc) msg+=' / '+desc;
	                	
	                }else{
	                	msg=desc;
	                }
		        	return msg;
		         }	        	
	         },
	         {
	        	 xtype: 'templatecolumn',
	             tpl:new Ext.XTemplate(
	        			 '{jn_dealTime}<br/>',
	        			 '{jn_name}<br/>',
	        			 '<tpl if="jn_attachs">',
	        			 '<a href="javascript:scanAttachs( \'{jn_attachs}\' ,\'{jn_name}\',\'{jn_dealManName}\')">附件({[this.getAttachCount(values)]})</a>',
	        			 /*' / 附件({[this.getAttachCount(values)]})',*/
	        	        '</tpl>',	        	       
	        			 {
	        				 disableFormats: true,
	        				 getAttachCount:function(v){
	        					 if(v.jn_attachs) return v.jn_attachs.match(/\;/gi).length;
	        				 }
	        			 }
	        	 ),
	             flex:0.55
	         }],
	         store: Ext.create('Ext.data.Store', {
	        	 fields: [{name: 'jn_id', type: 'string'},
	        	          {name: 'jn_name', type: 'string'},
	        	          {name: 'jn_dealManId', type: 'string'},
	        	          {name: 'jn_dealManName', type: 'string'},
	        	          {name: 'jn_dealManName', type: 'string'},
	        	          {name: 'jn_dealTime', type: 'string'},
	        	          {name:'jn_holdtime',type:'int'},
	        	          {name: 'jn_dealResult', type: 'string'},
	        	          {name: 'jn_operatedDescription', type: 'string'},
	        	          {name: 'jn_nodeDescription', type: 'string'},
	        	          {name: 'jn_infoReceiver', type: 'string'},
	        	          {name: 'jn_processInstanceId', type: 'string'},
	        	          {name: 'jn_attachs', type: 'string'},
	        	          {name: 'jn_attach', type: 'string'}//是否回退节点
	        	          ],
	        	          data: []
	         }), 
	         viewConfig :{
	        	 stripeRows:false,
	        	 trackOver: false,
	        	 plugins: [{
	        		 ptype: 'preview',
	        		 expanded: true,
	        		 pluginId: 'preview'
	        	 }]
	         },
	         initComponent : function(){
	        	 formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
	        	 var acountmaster=getUrlParam('newMaster'),nodeId=this.nodeId || formCondition.split("=")[1]; 
	        	 this.callParent(arguments);
	        	 if(!this.deferLoadData){
	        		 var processInstanceId=this.processInstanceId?this.processInstanceId:this.getProcessInstanceId(nodeId,acountmaster);
	        		 this.getOwnStore(processInstanceId,nodeId); 
	        	 }  
	         },
	         getProcessInstanceId:function (nodeId,acountmaster){
	        	 var processInstanceId=null;
	        	 Ext.Ajax.request({
	        		 url: basePath + 'common/getProcessInstanceId.action',
	        		 params: {
	        			 jp_nodeId : nodeId,
	        			 master:acountmaster,
	        			 k:0,
	        			 _noc:1
	        		 },
	        		 method:'post',
	        		 async:false,
	        		 callback: function(options,success,response){
	        			 var text = response.responseText;
	        			 var jsonData = Ext.decode(text);
	        			 processInstanceId= jsonData.processInstanceId;

	        		 }
	        	 });
	        	 return processInstanceId;
	         },
	         getOwnStore: function(processInstanceId,nodeid){	
	        	 var me = this;		
	        	 Ext.Ajax.request({
	        		 url : basePath + 'common/getAllHistoryNodes.action',
	        		 params: {
	        		 	processInstanceId:processInstanceId  ,
	        			 _noc:1
	        		 },
	        		 method : 'post',
	        		 callback : function(options,success,response){
	        			 var res = new Ext.decode(response.responseText),
	        			 nodes = res.nodes;
	        			 me.getStore().loadData(nodes);
	        		 }
	        	 });
	         }
});