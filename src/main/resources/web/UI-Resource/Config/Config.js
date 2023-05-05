function ExecuteSubmit(){
	var url=$("input#url").val();
	var method=$("select#method").val();
	var inboundData=$("textarea#inboundData").val();
	
	var strJSON ={"url":url,"method":method,"inboundData":inboundData};
	jQuery.ajax({
		url:"config/getDataFromDev",
		type:"POST",
		data:strJSON,
		success:function(obj){
			$("#returnData").val(obj.returnData);  
		},
		error:function(){
			alert("Execute failed ! Please check!");
		},
		complete:function(obj){

		}
	});
}

//collection new list tree

var contex_menu = {
    'context1': {
        elements: [{
            text: 'Add',
            icon: '',
            action: function(node) {
                //GetCurDeviceID();
            }
        }]
    },
    'context2': {
        elements: [{
            text: 'Delete',
            icon: '',
            action: function(node) {
                //						g_iCurChanIndex = parseInt(node.text.replace(/[^0-9]/ig,""));
                //						console.log("you right click the channel -> " + g_iCurChanIndex);
            }
        }]
    }
};

var mapProtocol = new Map();

function CreatProtocolTree(arrCollection) {
    var iProtocolFolderNum = arrCollection.length;
    var rootCollection = null;

    treeCollectionList = createTree('panelCollectionList', 'white', null);

    console.log(arrCollection);

    for (let arrItem of arrCollection) {
        rootCollection = treeCollectionList.createNode(arrItem.info.name, false, '/assets/images/deviceTree/folder.png', null, 'Folder Node', null);

        mapProtocol.set(rootCollection.text, arrItem.item)
        for (let objItem of arrItem.item) {
            LispFindItem(objItem, rootCollection);
        }
    }

    treeCollectionList.drawTree();
}

function LispFindItem(objItem, parentNode) {
    while (objItem.hasOwnProperty('item')) {
        var subRootCollection = parentNode.createChildNode(objItem.name, false, '/assets/images/deviceTree/folder.png', null, 'Folder Node', null);
        return (function(){
            LispFindItem(objItem.item, subRootCollection);
        }())
    }

    if(IsArray(objItem)){
        mapProtocol.set(parentNode.text, objItem);
        for(let subObjItem of objItem){
            console.log(subObjItem);
            CreatItemNode(subObjItem, parentNode);
        }
    }else{
        CreatItemNode(objItem, parentNode);
    }
}

function CreatItemNode(objItem, parentNode) {
    var sMethod = objItem.request.method;
    var sReqUrl = "";

    for (let objUrl of objItem.request.url.path) {
        sReqUrl += ("/" + objUrl);
    }

    var leafNode = parentNode.createChildNode(sMethod + "  " + sReqUrl, false, '/assets/images/deviceTree/file.png', parentNode, 'File Node', null)
}

function IsArray(obj) {
  return obj instanceof Array
}

CreatProtocolTree(objCollectionListInfo);

treeCollectionList.doubleClickNode = function(p_node) {
    if (p_node.tag == "Folder Node")
    {
        this.toggleNode(p_node);
    }
    else
    {
        var sSelectedUrl = p_node.text.split("  ")[1];
        console.log(sSelectedUrl);
        var arrProtocol = mapProtocol.get(p_node.parent.text);
        for(let objItem of arrProtocol)
        {
            if(!objItem.hasOwnProperty("request"))
            {
                continue;
            }

            var sReqUrl = "";
            for (let objUrl of objItem.request.url.path) {
                    sReqUrl += ("/" + objUrl);
                }

            if(sSelectedUrl == sReqUrl)
            {
                $("#method").get(0).value = objItem.request.method;
                $("#url").val(sReqUrl);
                $('#returnData').get(0).innerText = "";
                if(objItem.request.hasOwnProperty("body"))
                {
                    $("#inboundData").val(objItem.request.body.raw);
                }
                else
                {
                    $("#inboundData").val("");
                }
                break;
            }
        }

    }
}