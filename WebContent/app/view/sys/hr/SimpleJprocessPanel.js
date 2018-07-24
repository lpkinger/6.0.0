Ext.define('erp.view.sys.hr.SimpleJprocessPanel',{
	extend: 'Ext.view.View', 
	alias: 'widget.simplejprocesspanel', 
	id:'simplejprocesspanel',
	/*border:true,*/
/*	style: {
		position: 'absolute'
	},*/
	border: false,
	autoScroll:false,
	style: {
	    borderColor: '#c7c7c7',
	    borderStyle: 'solid',
	    background:'white',
	    position: 'absolute'
	},
	width:'100%',
	height:'86%',
	itemSelector:'li',
	activeItem:1,
	tpl:[
	     '<div class="simplejprogress" id="simplejprogress" style="border: 0px solid #F00;position: relative;">',
	     		'<div class="simplejpstart" id="simplejpstart">开始</div>',
	     		'<div class="simplejpcontent" id="simplejpcontent">',
	     		'<tpl for=".">',
	     		'<tpl if="this.isAssign(type)">',
	     			'<div class="node" id="jiddian" style="position: relative;">',
	     				'<div class="arrow"><svg xmlns="http://www.w3.org/2000/svg" version="1.1"><line x1="50" y1="0" x2="50" y2="45"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="42" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="58" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="50" y2="48"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/></svg></div>',
	     				'<div class="jpcontent" id="" style="position: relative"><span class="simplejprocess-delete" onclick="deletejp(this);" style="cursor:pointer;position:absolute;right:4px;top:-12px;"><img src="../jsps/sys/images/spjdelete.png"></img></span><span style="color:black;cursor:pointer;width:105px;height:50px;" class="assignInfo" name="assignInfo" data-code="{code}" data-name="{name}" data-type="{type}" data-contact="{contanct}"  onmouseover="show(this);" onmouseout= "hide(this);"  onclick="choiceInfo(this)">人员:{name}</span><span class="simplejprocess-add" style="cursor:pointer;position:absolute;right:4px;top:18px;" onclick="add(this);"><img src="../jsps/sys/images/spjadd.png"></img></span><div class="choicecontent" id="" style="position: absolute;z-index:98;left:-6px;background-color:white;top: 48px;line-height: 15px;display:none;"><a href="#" onclick="choiceMan(this);">人员</a><a href="#" onclick="choiceMan(this);">岗位</a><a href="#" onclick="choiceRole(this);">领导</a></div><span class="showmanInfo" id="" style="position: absolute;z-index:99;left:0px;background-color:rgb(199, 199, 199);border-radius: 5px;padding: 5px;font-size: 10px;top: 48px;line-height: 15px;display:none;min-width:100px;min-height:28px;color: #333;"></span></div>',
	     				/*'<div class="choicecontent" id="" style="position: absolute;z-index:99;left:230px;background-color:white;display:none;"><a href="#" onclick="choiceMan(this);">人员</a><a href="#" onclick="choiceMan(this);">岗位</a><a href="#" onclick="choiceRole(this);">角色</a></div>', <img src=<%=basePath%>/jsps/sys/images/arrows2.png"></img>*/
	     			'</div>',
	     			'</tpl>',
	     			'<tpl if="this.isCandidate(type)">',
	     			'<div class="node" id="jiddian" style="position: relative;">',
	     				'<div class="arrow"><svg xmlns="http://www.w3.org/2000/svg" version="1.1"><line x1="50" y1="0" x2="50" y2="45"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="42" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="58" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="50" y2="48"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/></svg></div>',
	     				'<div class="jpcontent" id="" style="position: relative"><span class="simplejprocess-delete" onclick="deletejp(this);" style="cursor:pointer;position:absolute;right:4px;top:-12px;"><img src="../jsps/sys/images/spjdelete.png"></img></span><span style="color:black;cursor:pointer;width:105px;height:50px;" class="assignInfo" name="assignInfo" data-code="{code}" data-type="{type}" data-name="{name}" data-contact="{contanct}"  onclick="choiceInfo(this)" onmouseover="show(this);" onmouseout= "hide(this);">岗位:{name}</span><span class="simplejprocess-add" style="cursor:pointer;position:absolute;right:4px;top:18px;" onclick="add(this);"><img src="../jsps/sys/images/spjadd.png"></img></span><div class="choicecontent" id="" style="position: absolute;z-index:99;left:-6px;background-color:white;top: 48px;line-height: 15px;display:none;"><a href="#" onclick="choiceMan(this);">人员</a><a href="#" onclick="choiceMan(this);">岗位</a><a href="#" onclick="choiceRole(this);">领导</a></div><span class="showmanInfo" id="" style="position: absolute;z-index:99;left:0px;background-color:rgb(199, 199, 199);border-radius: 5px;padding: 5px;font-size: 10px;top: 48px;line-height: 15px;display:none;min-width:100px;min-height:28px;color: #333;"></span></div>',
	     				/*'<div class="choicecontent" id="" style="position: absolute;z-index:99;left:230px;background-color:white;display:none;"><a href="#" onclick="choiceMan(this);">人员</a><a href="#" onclick="choiceMan(this);">岗位</a><a href="#" onclick="choiceRole(this);">角色</a></div>',*/
	     			'</div>',
	     			'</tpl>',
	     			'<tpl if="this.isRolAssignee(type)">',
	     			'<div class="node" id="jiddian" style="position: relative;">',
	     				'<div class="arrow"><svg xmlns="http://www.w3.org/2000/svg" version="1.1"><line x1="50" y1="0" x2="50" y2="45"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="42" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="58" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="50" y2="48"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/></svg></div>',
	     				'<div class="jpcontent" id="" style="position: relative"><span class="simplejprocess-delete" onclick="deletejp(this);" style="cursor:pointer;position:absolute;right:4px;top:-12px;"><img src="../jsps/sys/images/spjdelete.png"></img></span><span style="color:black;cursor:pointer;width:105px;height:50px;" class="assignInfo" name="assignInfo" data-code="{code}" data-type="{type}" data-contact="{contanct}" data-name="{name}"  onclick="choiceInfo(this)" onmouseover="show(this);" onmouseout= "hide(this);">领导:{name}</span><span class="simplejprocess-add" style="cursor:pointer;position:absolute;right:4px;top:18px;" onclick="add(this);"><img src="../jsps/sys/images/spjadd.png"></img></span><div class="choicecontent" id="" style="position: absolute;z-index:99;left:-6px;background-color:white;top: 48px;line-height: 15px;display:none;"><a href="#" onclick="choiceMan(this);">人员</a><a href="#" onclick="choiceMan(this);">岗位</a><a href="#" onclick="choiceRole(this);">领导</a></div><span class="showmanInfo" id="" style="position: absolute;z-index:99;left:0px;background-color:rgb(199, 199, 199);border-radius: 5px;padding: 5px;font-size: 10px;top: 48px;line-height: 15px;display:none;min-width:100px;min-height:28px;color: #333;"></span></div>',
	     				/*'<div class="choicecontent" id="" style="position: absolute;z-index:99;left:230px;background-color:white;display:none;"><a href="#" onclick="choiceMan(this);">人员</a><a href="#" onclick="choiceMan(this);">岗位</a><a href="#" onclick="choiceRole(this);">角色</a></div>',*/
	     			'</div>',
	     			'</tpl>',
	     			'<tpl if="this.isNull(type)">',
	     			'<div class="node" id="jiddian" style="position: relative;">',
	     				'<div class="arrow"><svg xmlns="http://www.w3.org/2000/svg" version="1.1"><line x1="50" y1="0" x2="50" y2="45"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="42" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="58" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="50" y2="48"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/></svg></img></div>',
	     				'<div class="jpcontent" id="" style="position: relative"><span class="simplejprocess-delete" onclick="deletejp(this);" style="cursor:pointer;position:absolute;right:4px;top:-12px;"><img src="../jsps/sys/images/spjdelete.png"></img></span><span style="color:black;cursor:pointer;width:105px;height:50px;" class="assignInfo" name="assignInfo" data-code="{code}" data-name="{name}" data-type="{type}" data-contact="{contanct}"  onclick="choiceInfo(this)" onmouseover="show(this);" onmouseout= "hide(this);">{name}</span><span class="simplejprocess-add" style="cursor:pointer;position:absolute;right:4px;top:18px;" onclick="add(this);"><img src="../jsps/sys/images/spjadd.png"></img></span><div class="choicecontent" id="" style="position: absolute;z-index:99;left:-6px;background-color:white;top: 48px;line-height: 15px;display:none;"><a href="#" onclick="choiceMan(this);" >人员</a><a href="#" onclick="choiceMan(this);">岗位</a><a href="#" onclick="choiceRole(this);">领导</a></div><span class="showmanInfo" id="" style="position: absolute;z-index:99;left:0px;background-color:rgb(199, 199, 199);border-radius: 5px;padding: 5px;font-size: 10px;top: 48px;line-height: 15px;display:none;min-width:100px;min-height:28px;color: #333;"></span></div>',
	     				/*'<div class="choicecontent" id="" style="position: absolute;z-index:99;left:230px;background-color:white;display:none;"><a href="#" onclick="choiceMan(this);">人员</a><a href="#" onclick="choiceMan(this);">岗位</a><a href="#" onclick="choiceRole(this);">角色</a></div>',*/
	     			'</div>',
	     			'</tpl>',
	     			
	     			'</tpl>',
	     			/*'<div class="node" id="jiddian">',
     					'<div class="arrow" id=""><img src="../jsps/sys/images/arrows2.png"></img></div>',
     					'<div class="jpcontent" id="" style="position: relative"><span class="simplejprocess-delete" style="cursor:pointer;position:absolute;right:4px;top:-12px;"><img src="../jsps/sys/images/spjdelete.png"></img></span><span style="color:red;cursor:pointer;width:100px;height:50px;" class="assignInfo" name="assignInfo" data-code="" onclick="choiceInfo(this)">指定人：张三FDSFASDFADSFDAFDF</span><span class="simplejprocess-add" style="cursor:pointer;position:absolute;right:4px;top:18px;"><img src="../jsps/sys/images/spjadd.png"></img></span></div>',
     					'<div class="choicecontent" id="" style="position: absolute;z-index:99;left:342px;background-color:white;display:none;"><a href="#" onclick="choiceMan(this);" >人员</a><a href="#" onclick="choiceJob(this);">岗位</a><a href="#" onclick="choiceRole(this);">角色</a></div>',
     			'</div>',*/
	     		'</div>',
	     		'<div class="arrow"><svg xmlns="http://www.w3.org/2000/svg" version="1.1"><line x1="50" y1="0" x2="50" y2="45"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="42" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="58" y2="38"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/><line x1="50" y1="47" x2="50" y2="48"style="stroke:rgba(51, 51, 51, 0.88);stroke-width:4"/></svg></div>',
	     		'<div class="simplejpend" id="simplejpstart">结束</div>',
	     '</div>',
	    {
	     isAssign: function(value){
	    	 if(value=="assignee") {
	    		 return true;
	    	 }else{
	    		 return false;
	    	 }
	     },
	     isCandidate: function(value){
	    	 if(value=="candidate-groups") {
	    		 return true;
	    	 }else{
	    		 return false;
	    	 }
	     },
	     isRolAssignee: function(value){
	    	 if(value=="rolAssignee") {
	    		 return true;
	    	 }else{
	    		 return false;
	    	 }
	     },
	     isNull: function(value){
	    	 if(value==""||value==null) {
	    		 return true;
	    	 }else{
	    		 return false;
	    	 }
	     },
	    }
	     ],
	     listeners:{ 
	    	
	     },
	     initComponent : function(){
	    	 var me=this;
	    	 me.store=Ext.create('Ext.data.Store', {
		    	 fields: [{name: 'itemId'},
		    	          {name:'name'},{name:'code'},{name:'type'},{name:'contanct'}],
		    	          data: [
		    	        	  /*{
		    	        		name:'',
		    	        		code:'',
		    	        		type:'',
		    	        		contanct:''
		    	  	 		}*/
		    	          ],
	    		});
	    	 this.callParent(arguments);
	     },
	     getData:function(id){
	    	this.store=Ext.create('Ext.data.Store', {
		    	 fields: [{name: 'itemId'},
		    	          {name:'desc'},{name:'type'}],
		    	          data: [
		    	        	  {
		    	  	 			desc:'基础资料',
		    	  	 			type:'normal'
		    	  	 		},{
		    	  	 			desc:'审批流',
		    	  	 			type:'normal'
		    	  	 		}
		    	          ],
	    		});
	 		return data;
	     }
});