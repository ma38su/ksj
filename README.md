# KSJ - 国土数値情報の変換プログラム

国土地理院で公開されている地図データを変換するためのプログラムです。

国土数値情報は、XML形式で配布されていますが、DOMで読み込むにはややデータ量が多いため、
SAXで読み込み、扱いやすいデータに変換するためのプログラムです。