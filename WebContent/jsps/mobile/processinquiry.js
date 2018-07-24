  var turnAll = function(gridId,condition){//全部采纳
		setLoading(true);
		$.ajax({
			url:basePath+"common/vastAgreeAllTurnPrice.action?_noc=1",
			type: 'POST',
			data: {caller:'ToPrice',id:current.jp_keyValue},
			success: function(result){	
			   setLoading(false);
			   $.showtip("全部采纳成功!", 2000);
			   loadNewGridStore(gridId,condition);
			},
			error:function(){
				setLoading(false);
			}
		});      
	};
	var unTurn = function(gridId,s,condition){//不采纳	
		setLoading(true);
		$.ajax({
			url:basePath+"common/vastNotAgreeTurnPrice.action?_noc=1",
			type: 'POST',
			data: {caller:'ToPrice',id:s.toString()},
			success: function(result){	
			   setLoading(false);
			   $.showtip("处理成功!", 2000);
			   loadNewGridStore(gridId,condition);
			},
			error:function(){
				setLoading(false);
			}
		});       
	};
	var turnVast = function(gridId,s,condition){//根据勾选结果进行采纳操作
		setLoading(true);
		$.ajax({
			url:basePath+"common/vastAgreeTurnPrice.action?_noc=1",
			type: 'POST',
			data: {caller:'ToPrice',id:s.toString()},
			success: function(result){	
			   setLoading(false);
			   $.showtip("处理成功!", 2000);
			   loadNewGridStore(gridId,condition);
			},
			error:function(){
				setLoading(false);
			}
		});      
	}
  function inquirydetail (i,data,colModel,condition){ 
         jQuery('#grid'+i).jqGrid({
					"hoverrows":false,
					"viewrecords":true,	
					"gridview":true,			
					"data": data,
					"scrollPaging":true,
					"shrinkToFit":false,
					"autoScroll": true,  
					"width":$('#topToolbar').width(),
					"rowNum":data.lenght,				
					"datatype": "local",
					"colModel":colModel,
					"pgbuttons":false,//是否显示翻页按钮
					"pginput":false,//是否显示跳转页面的输入框
					"multiselect" : true,//自定义多选
				    gridComplete:function(){
				    	var grid = $(this);  
				        var ids = grid.getDataIDs();  
				        for (var i = 0; i < ids.length; i++) {  
				            grid.setRowData( ids[i], false, {height: 35} );  
				        }  
				    },
					"pager":"#pager"+i
				});
				jQuery('#grid'+i).jqGrid('navGrid','#pager'+i,{add:false,edit:false,del:false,refresh:false},{reloadAfterSubmit:false},{},{},{multipleSearch:true}); 		
				jQuery('#grid'+i).navGrid('#pager'+i,{add:false,edit:false,del:false,refresh:false},{reloadAfterSubmit:false},{},{},{multipleSearch:true}
				 ).navButtonAdd('#pager'+i,{
				    caption : "不采纳",
				    id:'unturn',
				    buttonicon:'glyphicon glyphicon-remove-sign',
				    position:'first',
				    onClickButton : function() {
				       var s = jQuery("#grid"+i).jqGrid('getGridParam', 'selarrrow');
				       console.log(s);
					   if(s.length != 0){
					       unTurn('grid'+i,s,condition);
					   }else{
					    	$.showtip("请勾选需要处理的明细行!", 500);
					   }
				    }
				}).navButtonAdd('#pager'+i,{
				   caption : "全部采纳",
				    id:'turn',
				    buttonicon:'glyphicon glyphicon-ok-sign',
				    position:'first',
				    onClickButton : function() {
				       turnAll('grid'+i,condition);
				    }
				}).navButtonAdd('#pager'+i,{
				    caption : "采纳",
				    id:'turnVast',
				    buttonicon:'glyphicon glyphicon-ok-sign',
				    position:'first',
				    onClickButton : function() {
				    	var s = jQuery("#grid"+i).jqGrid('getGridParam', 'selarrrow');
					   if(s.length != 0){
					       turnVast('grid'+i,s,condition);
					   }else{
					       $.showtip("请勾选需要处理的明细行!", 500);
					   }				       
				    }
				});
				$('#pager'+i+'_left').width("60%");
				$('#pager'+i+'_center').width("0%");
				$('#pager'+i+'_right').width("40%");			
				$("#turn div span").removeClass("ui-icon");
				$("#unturn div span").removeClass("ui-icon");
				$("#turnVast div span").removeClass("ui-icon");
				//增加采纳和不采纳按钮
				/*$("#t_grid"+i).height(35).append(
                        "<button type='button' id='turn' style='height:30px;margin:2px 5px 2px 10px;border-radius:8px;vertical-align: middle;'>" +
                        "<span class='glyphicon glyphicon-ok-sign'></span>&nbsp;全部采纳</button>" +
                        "<button type='button' id='unturn' style='height:30px;margin:2px 5px 2px 10px;border-radius:8px;vertical-align: middle;'>" +
                        "<span class='glyphicon glyphicon-remove-sign'></span>&nbsp;不采纳</button>");*/
   }