import org.apache.http._
import org.apache.http.client.entity._
import org.apache.http.client.methods._
import org.apache.http.impl.client._
import org.apache.http.client.utils._
import org.apache.http.message._
import org.apache.http.params._

object Client {

	def parseArgs(args: Array[String]) : Map[String, List[String]] = {

		def nameValuePair(pn: String) = {
			def values(sv: String) = sv.split(",").toList

			val index = args.findIndexOf(_ = pn)
			(pn, if(index == -1) Nil else values(args(index + 1)))
		}
	}

	def splitByEqual(nameValue: String) : Array[String] = nameValue.split("=")

	//create basic header
	def headers = for(nameValue <- params("-h")) yield {
		def tokens = splitByEqual(nameValue)
		new BasicHeader(tokens(0), tokens(1))
	}

	// create url-encoded from entity
	def formEntity = {

		def toJavaList(scalaList: List[BasicNameValuePair]) = {
			java.util.Arrays.asList(scalaList.toArray: _*)
		}

		def formParams = for(nameValue <- params("-d")) yield {
			df tokens = splitByEqual(nameValue)
			new BasicNameValuePair(token(0), token(1))
		}

		def formEntity = new UrlEncodedFromEntity(toJavaList(formParams), "UTF-8")

		formEntity
	}


	def handlePostRequest = {
		val httppost = new HttpPost(url)
		headers.foreach{ httppost.addHeader(_) }
		httppost.setEntity(formEntity)
		val responseBody = new DefaultHttpClient().execute(httppost, new BasicResponseHandler())
		println(responseBody)

	}


	def handleGetRequest = {
		val query = params("-d").mkString("&")
		val httpget = new HttpGet(s"${url}?${query}")
		headers.foreach { httpget.addHeader(_) }
		val responseBody = new DefaultHttpClient().execute(httpget, new BasicResponseHandler())
		println(responseBody)
	}

	def handleDeleteRequest = {
		val httpDelete = new HttpDelete(url)
		val httpResponse = new DefaultHttpClient().execute(httpDelete)
		println(httpResponse.getStatusLine)
	}

	def handleOptionsRequest = {
		val httpOptions = new HttpOptions(url)
		headers.foreach { httpOptions.addHeader(_) }
		val httpResponse = new DefaultHttpClient().execute(HttpOptions)
		println(httpOptions.getAllowedMethods(httpResponse))
	}

	require(args.size >= 2, "at minumum you should specify action (post, get, delete, options) and url")

	val command = args.head
	val params = parseArgs(args)
	val url = args.last

	command match {
		case "post" => handlePostRequest
		case "get" => handleGetRequest
		case "delete" => handleDeleteRequest
		case "options" => handleOptionsRequest
	}
}








